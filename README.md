# TransportPCE (Highstreet Fork)

Optical transport controller based on [OpenDaylight](https://www.opendaylight.org) (MD-SAL) for OpenROADM networks: topology management, service creation (RDM-2-RDM), path computation and rendering onto XPDRA/ROADM devices.

This fork (`stable/vanadium`, version 12.1.0) extends the OpenDaylight community edition of TransportPCE with four major features:

1. **SB-RESTCONF client** — type-safe southbound device access via RESTCONF instead of NETCONF
2. **Extended NBI notifications** — VES event publication to Kafka for topology and service-path changes
3. **Node connection states via Kafka** — consuming connection events from external controllers with automatic topology reconciliation
4. **GNPy integration** — integration of the official GNPy REST API (oopt-gnpy-api v1.0) for physical path validation

---

## Architecture Overview

```
                    ┌────────────────────────────────────────────┐
   NBI (REST)  ───► │  TransportPCE (Lighty runtime, MD-SAL)     │
                    │                                            │
                    │  servicehandler ──► pce ──► renderer       │
                    │       │              │                     │
                    │       │         (GNPy REST)                │
                    │       ▼                                    │
                    │  nbinotifications ──► Kafka publisher      │
                    │  device-discovery  ◄── Kafka consumer      │
                    │       │                                    │
                    │       ▼                                    │
                    │  sb-restconf-client ──► devices (RESTCONF) │
                    └────────────────────────────────────────────┘
```

| Module | Purpose |
|---|---|
| `api` | YANG models (TransportPCE API, GNPy models, service-path-list) |
| `common` | Shared utilities, network transactions, JSON codecs |
| `networkmodel` | Topology model (openroadm-network, openroadm-topology) |
| `servicehandler` | Service orchestration; writes `ServicePaths` after path computation |
| `pce` | Path computation (internal algorithm + GNPy consumer `org.opendaylight.transportpce.pce.gnpy`) |
| `renderer` | Device configuration along the computed path |
| `nbinotifications` | NBI notifications: subscription RPCs, Kafka publisher, VES serialization |
| `device-discovery` | Topology reconciliation, Kafka consumer for connection states |
| `sb-restconf-client` | Southbound RESTCONF client (device mount, GET/PUT/PATCH/DELETE) |
| `tapi` | TAPI interface |
| `lighty` / `distribution/transportpce-lighty` | Lighty runtime (startup without Karaf), controller Docker image |
| `distribution/gnpy` | Docker image of the GNPy REST API server (oopt-gnpy-api v1.0) |

The Karaf/OSGi modules (`features`, `karaf`) are disabled in this fork; the runtime is **Lighty** (`lighty/`, entry point `io.lighty.controllers.tpce.Main`).

---

## 1. SB-RESTCONF Client (`sb-restconf-client`)

Type-safe southbound client for device-level operations over RESTCONF — as an alternative to the classic NETCONF connection.

**Key classes:**

- `SbRestconfClient` — generic `get`, `put`, `patch`, `delete` operations on mounted device data. Callers work exclusively with generated YANG binding classes (`DataObject`, `DataObjectIdentifier`); JSON serialization is handled internally via a `BindingDataCodec`.
- `SbRestconfProvider` — lifecycle: loads the configuration (`etc/org.opendaylight.transportpce.cfg`), creates the `ControllerUuidResolver` (reads the controller UUID from MD-SAL) and starts the client. Works both in OSGi/Blueprint and in Lighty.
- `RestDeviceTransactionManager` / `RestMountPoint` — per-device transaction handling, analogous to the NETCONF device manager.
- `ControllerUuidResolver` — maps device node IDs to the responsible controller.

**URL pattern:**

```
{controller-base-url}{mount-prefix}{node-id}/yang-ext:mount{object-path}
```

**Integration:** when remote controllers are configured (`controller.list`), the Lighty runtime (`TransportPCEImpl`) wires the `SbRestconfClient` into a `RestDeviceTransactionManager`, which takes the place of the classic NETCONF `DeviceTransactionManager` — the whole service pipeline (network model, renderer, service handler) then talks to devices through it.

Technical notes: Jersey client with Jetty connector (required for HTTP `PATCH`, which `HttpURLConnection` does not support).

---

## 2. Extended NBI Notifications (`nbinotifications`)

The notification module provides the classic subscription RPCs (`create-notification-subscription-service`, `get-notifications-...`, TAPI/alarm/process services) and has been extended with a **VES event pipeline to Kafka**:

**Pipeline:**

1. `NbiNotificationsProvider` registers generic `DataTreeChangeToVesListener<T>` on data-tree paths:
   - `Link` in `openroadm-topology` (CONFIGURATION) — topology changes
   - `ServicePaths` in `service-path-list` (OPERATIONAL) — service routes after path computation
2. The listener maps modification types to change types (`created` / `updated` / `deleted`) and builds a simplified event object (only the fields a consumer needs — e.g. for links only `link-id` + source/destination node, filtered to ROADM-to-ROADM links).
3. `VesEventSerializer` serializes to RFC7951 JSON via the MD-SAL codec.
4. `Publisher` (Kafka producer) sends the event in a VES envelope:
   - domain `notification`, `changeIdentifier` = fully qualified binding class name
   - payload in `notificationFields.additionalFields`
   - Kafka key = entity ID (link-id or service-path-name)
   - topic configurable, default `unauthenticated.VES_NOTIFICATION_OUTPUT`

**Example payload (ServicePaths event, additionalFields):**

```json
{
  "service-path-name": "service-1",
  "path-description": {
    "aToZ-direction": { "aToZ": [ { "id": "0", "resource": { "node-id": "node-1" } } ] },
    "zToA-direction": { "zToA": [ { "id": "0", "resource": { "node-id": "node-2" } } ] }
  }
}
```

Consumers can read the events directly from Kafka (ONAP-VES-compatible format); hop resources serialize flat as `node-id` | `link-id` | `tp-id`+`tp-node-id`.

---

## 3. Node Connection States via Kafka (`device-discovery`)

TransportPCE keeps its topology up to date by **consuming connection-state events of external controllers from Kafka** — instead of relying on NETCONF mount events.

**Key classes:**

- `VesKafkaConsumer` — Kafka consumer (property `kafka.topic`, default `unmNotifications`). Processes only VES events with `changeType == "ConnectionState"`; events carrying this instance's own controller UUID are ignored (loop protection).
- `VesEventWrapper` / `VesEventWrapperDeserializer` — type-safe access to `reportingEntityId` (controller UUID), `changeIdentifier` (node ID) and `newState` (`connecting` / `connected` / `disconnected` / `removed`).
- `TopologyReconciliationService` — initial reconciliation at startup: reads the node list of all configured controllers via RESTCONF and writes the full node data into MD-SAL.
- `IetfNetworkRestconfClient` / `NetconfTopologyRestconfClient` — RESTCONF access to the controllers' topology data models.
- `TopologyWriter` — writes/deletes nodes (including `connection-status`, host/port, controller UUID) into the `netconf-node-topology` model in MD-SAL.

**Behavior per event:**

| newState | Action |
|---|---|
| `connected`, `connecting` | Fetch full node data from the controller (RESTCONF) and write it to MD-SAL; fallback: minimal node entry with status `connecting` |
| `disconnected`, `removed` | Delete the node from the MD-SAL topology |
| other | Event is discarded (warning log) |

Events from unknown controller UUIDs are ignored. This keeps the device topology inside TransportPCE automatically in sync with the connection state of the devices at the connected controllers.

---

## 4. GNPy Integration (`pce` + `distribution/gnpy`)

Integration of the **official GNPy REST API** ([oopt-gnpy-api](https://github.com/Telecominfraproject/oopt-gnpy-api) v1.0 with GNPy core 2.14) for physical path validation (SNR/impairment evaluation) — replacing the older Orange fork (`gnpy_tpce` 2.4.7).

**Key components:**

- Java package `org.opendaylight.transportpce.pce.gnpy` in the `pce` module:
  - `GnpyConsumerImpl` — HTTP client for `GET /api/v1.0/status` and `POST /api/v1.0/path-request` (port 8080, OpenAPI UI at `/docs`)
  - `GnpyUtilitiesImpl` — request assembly from the MD-SAL topology (YANG format `gnpy-api:api`, services + topology as augments), including equipment library
  - `GnpyServiceImpl` — orchestration, path constraints (including GNPy guard-band clamping at the C-band edge), synchronization handling only for multi-requests
  - `GnpyEquipmentLoader` — loads the OpenROADM v5 equipment library from `pce/src/main/resources/gnpy/eqpt_config_yang.json` and merges it into the request if the request carries no equipment itself
- YANG models in `api/src/main/yang/` (official 2025 models): `gnpy-api@2025-06-13`, `gnpy-network-topology@2025-03-01`, `gnpy-path-computation@2025-01-20`, `gnpy-eqpt-config@2025-05-26`
- `distribution/gnpy` — Maven module that builds the GNPy API server image via the docker-maven-plugin: clones `oopt-gnpy-api` (repo/tag configurable via `-Dgnpy.git.repo` / `-Dgnpy.git.tag`) and builds its Dockerfile → image `hightec/gnpy`.

**Flow:** for a service path computation, `PathComputationServiceImpl` passes the candidate paths (internal PCE computation, see below) together with the topology and service parameters to GNPy. GNPy validates the paths physically (SNR, constraints, spectrum) and returns `201` with `response[]` — including `no-path` with metric-based reasons if a path is not physically feasible.

The **internal PCE path computation** (Yen K-shortest-paths, JGraphT, k=15, first-fit selection) remains in place; GNPy provides the physical validation/evaluation of the candidates.

**Configuration:** `gnpy.url`, `gnpy.username`, `gnpy.password` in the config file (see below). Test fixtures in `pce/src/test/resources/gnpy/`, E2E integration test `lighty/.../GnpyTest.java` (skipped via Assume when no GNPy server is running).

---

## Configuration

Central configuration file: **`etc/org.opendaylight.transportpce.cfg`** (properties format, supports `${env:VAR:-default}` placeholders).

| Property | Default | Meaning |
|---|---|---|
| `kafka.bootstrap.servers` | `localhost:9092` | Kafka broker (publisher + consumer) |
| `kafka.topic` | `unmNotifications` | Topic for incoming connection-state events |
| `kafka.group.id` | `transportpce-device-discovery` | Consumer group |
| `ves.topic` | `unauthenticated.VES_NOTIFICATION_OUTPUT` | Publisher topic for outgoing VES events |
| `ves.reporting.entity.id` | – | Own controller UUID (loop protection) |
| `ves.reporting.entity.name` | `transportpce` | Reporting entity name in the VES header |
| `ves.source.name` | `networkmodel` | Source name in the VES header |
| `controller.list` | – | Comma-separated list of controller UUIDs |
| `controller.<uuid>.baseurl` | – | REST endpoint per controller |
| `controller.bearer.token` | `change-me` | Bearer token for controller REST access |
| `mount.prefix` | standard ODL mount path | Mount prefix for device access |
| `gnpy.url` | – | GNPy API server URL (e.g. `http://gnpy:8080`) |
| `gnpy.username` / `gnpy.password` | – | GNPy auth (optional) |
| `preload.dir` | – | Directory for topology preload at startup |


If `controller.list` is empty, transportpce will choose NETCONF as southbound interface. For `mount.prefix` there are at the moment two options:
  - `/data/ietf-network:networks/network=unm-topology/node=` for ietf-network based controller
  - `/data/network-topology:network-topology/topology=topology-netconf/node=` for network-topology based controllers like opendaylight

---

## Logging

All modules log through **SLF4J** (Log4j2 as the backend in the Lighty runtime). The configuration lives in `lighty/src/main/resources/log4j2.properties` and is bundled into the Lighty distribution; output goes to the console (stdout) with pattern

```
%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] (%F:%L) - %m%n
```

(timestamp, level, thread, file:line, message).

**Log levels per namespace** — every logger level supports environment-variable overrides with the syntax `${env:VAR:-default}`, so levels can be tuned per deployment without touching the file:

| Environment variable | Logger namespace | Default |
|---|---|---|
| `LOG_ROOT_LEVEL` | root logger | `INFO` |
| `LOG_DD_LEVEL` | `org.opendaylight.transportpce.devicediscovery` | `INFO` |
| `LOG_SB_LEVEL` | `org.opendaylight.transportpce.sbrestconf` | `INFO` |
| `LOG_NM_LEVEL` | `org.opendaylight.transportpce.networkmodel` | `INFO` |
| `LOG_COMMON_LEVEL` | `org.opendaylight.transportpce.common` | `INFO` |
| `LOG_PCE_LEVEL` | `org.opendaylight.transportpce.pce` | `INFO` |
| `LOG_SH_LEVEL` | `org.opendaylight.transportpce.servicehandler` | `INFO` |
| `LOG_TAPI_LEVEL` | `org.opendaylight.transportpce.tapi` | `INFO` |
| `LOG_LIGHTY_LEVEL` | `io.lighty` | `INFO` |
| `LOG_MDSAL_LEVEL` | `org.opendaylight.mdsal` | `INFO` |
| `LOG_YANGTOOLS_LEVEL` | `org.opendaylight.yangtools` | `WARN` |

Example: start the controller with debug logging for the PCE module only:

```bash
LOG_PCE_LEVEL=DEBUG ./start-controller.sh
```

**Useful log points for troubleshooting:**

- **GNPy requests:** `GnpyConsumerImpl` logs the complete request body on INFO (`requestbody = {…}`). In case of GNPy errors (HTTP 500, unexpected `no-path`), extract the body from the log and replay it directly against the GNPy server to reproduce the issue offline.
- **Device discovery:** `VesKafkaConsumer` logs every processed connection-state event on INFO (`VES event: controller=…, node=…, state=…`); discarded events (unknown controller, own UUID, invalid fields) are visible on DEBUG/WARN.
- **VES publisher:** the `nbinotifications` publisher logs topic and Kafka key per sent event.

For custom deployments the log4j2.properties file can be replaced in the distribution's `etc/` directory (e.g. to add file appenders or a log aggregator).

---

## Build & Start

**Prerequisites:** JDK 21, Maven 3.9.x.

```bash
# Build the main reactor (without tests):
mvn clean install -DskipTests

# With tests:
mvn clean install

# Single modules (example):
mvn -pl pce test
```

**Lighty runtime** (`lighty/` is outside the main reactor — TransportPCE artifacts must be installed first):

```bash
cd lighty
./build.sh                 # builds the Lighty distribution
# Start: see lighty/README.md (starts TransportPCE without Karaf)
```

**Docker images:**

```bash
# GNPy API server image:
cd distribution/gnpy && mvn clean install
# (clones oopt-gnpy-api v1.0 and builds its Dockerfile → hightec/gnpy)

# TransportPCE Lighty image:
cd distribution/transportpce-lighty && mvn clean install
```

**Deployment note:** the four features work together with external components — a Kafka broker, external controllers (RESTCONF) and a GNPy server. The corresponding container definitions (docker-compose, Kubernetes) live in the separate `int-deployment` repository.

---

## Tests

- Unit tests per module (`mvn -pl <module> test`); Wiremock stubs the GNPy endpoints for consumer tests.
- Integration tests in `tests/` (require devices/containers).
- `lighty/.../GnpyTest.java`: E2E against a running GNPy server (skipped via `Assume` if none is reachable).

## License

Eclipse Public License v1.0 — see the source file headers. Base: [OpenDaylight TransportPCE](https://git.opendaylight.org/gerrit/admin/repos/transportpce) (community edition), extended by Highstreet Technologies GmbH.
