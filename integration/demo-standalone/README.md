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

### End2End test

For creation of service 1

```
../bin/integration.py test end2end
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

 * additonal prerequisites:
    * ansible (pls install with pip)

 * autogenereate the data models for the roadms and xpdrs
```
../bin/createNTSdevices.py \
  --nodes ../topology-info/Nodes_Germany_17.json \
  --links ../topology-info/Links_Germany_17.json \
  --output-profile germany-17 \
  --output-folder demo-standalone/conf-generated \
  --sim 'nts-ng'   ## For creation of nts-ng image files

```
* edit the sim-deployment/hosts.ini

* check that your remote hosts are accessable with ansible
```
ansible -i sim-deployment/hosts.ini -m ping all
```
 * deploy simulators with the models (NTS)
```
cd sim-deployment
./deploy-sims.py deploy --src ../demo-standalone/conf-generated/ --profile germany-17
```

 * deploy simulators with the NTS-NG models
```
cd sim-deployment
./deploy-sims.py deploy-ng --src ../demo-standalone/conf-generated/ --profile germany-17-ng
```

 * run end2end test with created profile
```
../bin/integration.py test end2endbb --profile germany-17
```
```
../bin/integration.py test end2endbb --profile germany-17-ng  ## with NTS-NG devices
```
