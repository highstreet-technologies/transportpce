# Infrastructure Integration
## Simulators

 * PyNTSimulator-openroadm https://git-highstreet-technologies.com/highstreet/pynts

### Generating Device Files and Docker Compose
To generate device files and the Docker Compose configuration for simulators, use the `createNTSdevices.py` script located in the `bin` folder. This script processes topology files (e.g., `Nodes_Germany_17.json` and `Links_Germany_17.json`) to generate the necessary configurations for the simulators.

**⚠️ Ensure you are in the `integration` folder before executing the command.**

Example command:
```
./bin/createNTSdevices.py \
  --nodes topology-info/Nodes_Germany_17.json \
  --links topology-info/Links_Germany_17.json \
  --output-profile germany-17 \
  --output-folder demo-standalone/conf-generated
```

### Demo-Standalone Folder
The `demo-standalone` folder contains configurations and scripts for running a standalone integration of ONAP SDNC with TransportPCE and PyNTS OpenROADM simulators. It includes:
- Auto-generated device files and Docker Compose configurations.
- Scripts for deploying and testing the simulators.
- Environment configurations for remote and local setups.

For more details on how to run the service creation tests, refer to the [demo-standalone README](demo-standalone/README.md).

## Transport PCE Preparation (for future after odl update)

 * compile all sources (maybe with skipTests): mvn clean install
 * build transportpce and gui docker image (distribution folder)
 * feel free to exchange the sdnc-image with your preferred one (>=ONAP honolulu release)

## Integration script

The integration script is located in the bin folder and called ```integration.py```. It has to be called from inside of the integration folder you like to execute(e.g. onap-ext) because its docker-compose naming is related to the subfolder. This script automaticall detect the IPs of the images and will do e.g. a mount with the correct parameters, so that you don't have to care about all these things. I don't wanted to have static IPs in case on some machines some subnetworks are already in use. So with default docker-compose will find one for itself.

```
$ ../bin/integration.py info

name                ip                  running
============================================================
sdnr                172.20.0.3          running
sdncweb             172.20.0.9          running
transportpce        172.20.0.7          running
roadma              172.20.0.8          running
roadmb              172.20.0.10         running
roadmc              172.20.0.6          running
xpdra               172.20.0.4          running
xpdrc               172.20.0.5          running
```

If you like to have the ODLUX Gui started in you browser you can also directly execute ```../bin/integration.py web```. In this example it will call ```http://172.20.0.9:8080``` in your browser. Username and password are the configured ones. The same as for the restconf interface.




### Commands

| command | description |
| ------- | ----------- |
| ../bin/integration.py info | show ip info for all containers |
| ../bin/integration.py status | show ip info for all containers |
| ../bin/integration.py mount | show ip info for all containers |
| ../bin/integration.py unmount | show ip info for all containers |
| ../bin/integration.py isready | check ready state of sdnc and transportpce container (every bundle is state!=failure) |
| ../bin/integration.py setlogs | set logs inside of sdnc and transportpce to DEBUG |
| ../bin/integration.py caps [devicename] | show yang-capabilities for the mounted device |
| ../bin/integration.py getlogs | copy karaf logs of sdnc and transportpce into logs/ folder |
| ../bin/integration.py web | open ODLUX gui of sdnc in the browser|
| ../bin/integration.py webtrpce | open transportpce gui in the browser |
| ../bin/integration.py apidocs [sdnc\|trpce] | open sdnc or transportpce apidcos gui in the browser |

### overrides

| arg | default value | description |
| --- | ------------- | ----------- |
| --profile | default | uses {value}.json to load sim and controller information from configs folder |
| --env | .env | load env vars for passwords etc |




## Limitiations

 * interface types only for org-openroadm-interface supported