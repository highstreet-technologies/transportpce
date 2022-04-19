# Integration: ONAP SDNC with transportPCE and Honeynode Simulators

## Prerequisites

  * NTSimulator for OpenROADM with 2.2.1 Model (hightec/ntsim_openroadm_2.2.1_standalone:0.6.5)

## Configure
Just config the params in the ```.env``` file.

```
REMOTE_ODL_ENABLED=true
```

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


### End2End for Germany-17 backbone network

 * autogenereate the data models for the roadms and xpdrs
```
../bin/createNTSdevices.py \
  --nodes topology-info/Nodes_Germany_17.json \
  --links topology-info/Links_Germany_17.json \
  --profile profiles/sims/germany-17.json \
  --output-folder demo-standalone/conf-generated

```
 * edit the sim-deployment/hosts.ini

 * deploy simulators with the models
```
python3 sim-deployment deploy demo-standalone/conf-generated
```

 * run end2end test with crteated profile
```
../bin/integration.py test end2endbb --profile germany-17
```