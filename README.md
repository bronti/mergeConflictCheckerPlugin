## Merge Conflict Checker

### Build

Run ```mvn package``` from the root directory to build the plugin. Resultind package ```<artifactId>.zip``` will be placed in ```target``` directory. 

### Install

To install the plugin put ```<artifactId>.zip``` to ```plugins``` directory in TeamCity data directory (```~/.BuildServer``` by default) and restart the server.