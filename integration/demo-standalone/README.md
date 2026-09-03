# Integration: ONAP SDNC with transportPCE and Pynts Openroadm Simulators

## Prerequisites

  * PyNTSimulator for OpenROADM with 7.1.0 Model (registry.t1.lab.osn-lab.com/hightec/pynts-openroadm-v7_1_0:latest)

## Configure
Just config the params in the ```.env``` file.

```
REMOTE_ODL_ENABLED=true
```

## How to start

 * start the containers

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
 * deploy simulators with the models (PyNTS)


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


