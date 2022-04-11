# Integration: ONAP SDNC with transportPCE and Honeynode Simulators

## Prerequisites

  * NTSimulator for OpenROADM with 2.2.1 Model (hightec/ntsim_openroadm_2.2.1_standalone:0.6.5)

## Configure
Just config the params in the ```.env``` file.

```
REMOTE_ODL_ENABLED=true
```
### Create the device models for Germany-17 backbone network
Execute script integration/bin/createNTSdevices.py. Device models w.r.t ROADMs and XPDRS should be created inside the folder _integration/demo-standalone/conf-generated_. There should be also a docker-compose file created as integration/demo-standalone/docker-compose-germany17.yml.
In case the original docker-compose file contains the simple topology used for transportPCE testing, rename it to something else and rename docker-compose-germany17.yml to docker-compose.yml
## How to start

```
docker-compose up -d
```

## Tests
### To test with the Germany-17 network, change default profile to germany profile by using
```
--profile "germany-17"
```
### Mount remotely

```
../bin/integration.py --profile "germany-17" mount
```

### End2End test

For creation of service 1

```
../bin/integration.py  --profile "germany-17"  test end2end
```
for remote enabled=true
```
# set log levelsS
../bin/integration.py setlogs
# stops openroadm devicemanager provider (due a bug)
../bin/integration.py bstop 207
# start test
../bin/integration.py test 3
```

if it fails and you need logs
```
../bin/integration.py getlogs
```
