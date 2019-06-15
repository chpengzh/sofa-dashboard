#!/bin/bash

echo 'Patch MYSQL DB host to sofa-dashboard-db...'
sed '/db_url/s/localhost/sofa-dashboard-db/g' /apollo-quick-start/demo.sh.bak \
    > /apollo-quick-start/demo.sh

# print for debug
# cat /apollo-quick-start/demo.sh

/apollo-quick-start/demo.sh start

tail -f -n -1000 /apollo-quick-start/portal/apollo-portal.log