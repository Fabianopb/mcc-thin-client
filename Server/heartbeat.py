
import time
import sys
import libcloud
from libcloud.compute.types import Provider
from libcloud.compute.providers import get_driver

def main():

    if len(sys.argv) != 2:
        exit
    running_node_name = sys.argv[1]

    time.sleep(60*30) # 30 minutes

    # Initialize libcloud Google Compute Engine Driver using service account authorization
    ComputeEngine = get_driver(Provider.GCE)
    gce = ComputeEngine('860271242030-compute@developer.gserviceaccount.com', 'key/mcc-2016-g13-p1-290f94a963cb.json',
                        datacenter='europe-west1-d', project='mcc-2016-g13-p1')

    node = gce.ex_get_node(running_node_name)
    gce.ex_stop_node(node)


main()
