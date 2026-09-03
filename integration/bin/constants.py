import os

CURRENT_PATH=os.getcwd()
TRPCEPATH=os.path.abspath(os.path.dirname(os.path.realpath(__file__))+'/../../')
BIN_FOLDER=TRPCEPATH+"/integration/bin"
PROFILES_SIM_FOLDER=TRPCEPATH+"/integration/profiles/sims"
PROFILES_CONTROLLER_FOLDER=TRPCEPATH+"/integration/profiles/controllers"
NETWORK_NAME='oam-network'
