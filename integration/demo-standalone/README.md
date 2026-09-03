# Integration: ONAP SDNC with transportPCE and Pynts Openroadm Simulators

## Prerequisites

  * NTSimulator for OpenROADM with 2.2.1 Model (hightec/ntsim_openroadm_2.2.1_standalone:0.6.5)

## Configure
Just config the params in the ```.env``` file.

```
REMOTE_ODL_ENABLED=true
```
## Autogenerate device files, docker compose and profiles
```
cd integration
```
```
../bin/createNTSdevices.py \
  --nodes ../topology-info/Nodes_Germany_17.json \
  --links ../topology-info/Links_Germany_17.json \
  --output-profile germany-17 \
  --output-folder demo-standalone/conf-generated

  ```

## How to start
```
cd integration/demo-standalone
```

```
docker-compose -f docker-compose-generated.yml up -d
```

## Tests

### End2End test

For creation of service with germany-17 backbone network


```
../bin/integration.py test end2endbb --profile germany-17-ng
```



### End2End Ansible for Germany-17 backbone network

 * additonal prerequisites:
    * ansible (pls install with pip)

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

 * Log settings for remote enabled=true

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


