#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function usage {
    echo "usage: $0: <customers|notification|order|payment|product> <attacks_enable_exception|attacks_enable_killapplication|attacks_enable_latency|attacks_enable_memory|watcher_enable_component|watcher_enable_controller|watcher_enable_repository|watcher_enable_restcontroller|watcher_enable_service|watcher_disable>"
    echo "Example"
    echo "./chaos/call_chaos.sh customers attacks_enable_exception watcher_enable_restcontroller"
    exit 1
}

if [[ $# -lt 2 ]]; then
    usage
fi

export PORT="${PORT:-}"

while [[ $# > 0 ]]
do
key="$1"
case $1 in
    customers)
        PORT=8020
        ;;
    notification)
        PORT=8030
        ;;
    order)
        PORT=8040
        ;;
    payment)
        PORT=8050
        ;;
    product)
        PORT=8060
        ;;
    attacks*)
        ( cd "${ROOT_DIR}" && curl "http://localhost:${PORT}/actuator/chaosmonkey/assaults" -H "Content-Type: application/json" --data @"${1}".json --fail )
        ;;
    watcher*)
        ( cd "${ROOT_DIR}" && curl "http://localhost:${PORT}/actuator/chaosmonkey/watchers" -H "Content-Type: application/json" --data @"${1}".json --fail )
        ;;
    *)
        usage
        ;;
esac
shift
done
