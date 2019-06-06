# K8-ConRet-Creator


##### Stable-Version: 2.8

The ConRetCreator is a tool to manage your environmentsettings in your k8-store as kubernetes or openshift without a 
local installation of kubctl or oc.
It will help you to create sets of secrets and configmaps from a defined set as yaml-Input.
If a value is empty, CLI will ask you interactive for this value or take it from exported ENV-Variable. 

With this logic definitions can be stored and distributed in a VCS. Furthermore, it can be avoided that passwords, for example, are not stored.

### Usecases

#### Usecase-1:
- You may **not store your KeyValues in** a file system or Version control system (VCS) for compliance or other reasons. 
- And/or these KeyVaules may only be **set by certain persons**.

#### Usecase-2:
- You have some files that are stage-specific, but are have the same name at runtime.

#### Conclusion
To add values to your store, you need your **input file**, maybe your dependent files and Java-based tool "**conretcreator.jar**". 
In this case, you can give this all by an email or by vcs to a other guy (Production) to setup your Store.

[![ConRetCreator](docs/conretcreator_small.png)](docs/conretcreator.png)

>[Click for Zoom](docs/conretcreator.png)


## Install
```
    git clone https://github.com/dockermgeo/k8s-conretcreator.git
    make install
    # TODO -> define Input-Yaml
```

## Dependencies
- JDK 1.8 or higher
- Lower versions than **2.4** of conretcreator depends a installed **oc** or **kubectl** on your System.   

## SYNTAX
``` 
    java -jar conretcreator.jar -<option> -i <INPUTFILE(s)>
```

## OPTIONS


| Option        | Description |
| ------------- |-------------| 
| **-i FILE-A FILE-B**      | **Inputfile or -files** in YAML-Format |
| -c      | Create/Replace a ConfigMap |
| -s      | Create/Replace a Secret  |
| -f      | Create/Replace Secrets from filesystem |
| -r      | Create/Replace Path Router |
| -a      | Create/Replace of all above |
| -h      | help, show usage|
| -x      | export/display section defintion for container-env |
| -v      | make all cli inputs hidden |
| -z      | debug - do not delete any generated files and show commands |



More ***Versions*** a distributed in folder [releases/](releases/). 
You can use our Scripts, "run.sh" for OSx/Linux or "run.bat" on Windows.


### Input-YAML

Empty **values** will filled by CLI.

```
    clustername: "https://cluster-dev.mgeo.de"
    namespace: "play-with-configs"
    name: "mgeo-dev-io"
    # kind: Secret or ConfigMap
    kind: Secret
    
    # Datablock for configmap
    configs:
      - name: loglevel
        value: debug
        desc: "Loglevel for the APP"
    
    # Datablock for secrets
    secrets:
      - name: username
        value: admin
        desc: "Name of User"
      - name: password
        # if value is empty, CLI will ask for it
        value: ""
        desc: "Password will set by CLI"

    # FILES as secret
    secretfiles:
    # relativepath to this file
      - src: "files/stages/log4j-prod.properties"
        target: "log4j.properties"
      - src: "files/debugger.properties"
    # no target means, take the original name
        target: ""
# Additional Route-entries
routes:
  - host: "myhost"
    path: "/path1"
    targetservice: "myapp"
  - host: "myhost"
    path: "/path2"
    targetservice: "myapp"
```

## Get output for k8s-definiton 

With the **parameter -x** the necessary Templatedefinition can be output to Stdout.

```
    containers:
    .
    .
    env:
    - name: LOGLEVEL
      valueFrom:
      ... 
```

Usage as secret
```
    - name: USERNAME
      valueFrom:
        secretKeyRef:
            name: mgeo-dev-secs
            key: username
```

Usage as configmap
```
    - name: LOGLEVEL
      valueFrom:
        configMapKeyRef:
            name: mgeo-dev-cfg
            key: loglevel
```

Usage secrets from files
 ```
    env:
        volumeMounts:
            - name: mgeo-dev-volume
              mountPath: /etc/secrets
              readOnly: true
    volumes:
        - name: mgeo-dev-volume
            secret:
                secretName: mgeo-dev

 ```
 
 
 
## Example / Usecase

I defined some [special Variable](src/main/resources/) values in fr-EXAMPLE-<***STAGENAME***>.yaml and some different [files](src/main/resources/files) who I like to use in different runstages.
Show as it works.

```
./run.sh -i sets/EXAMPLE-dev.yaml -c -s f -r  -z 

Working on:
	- CLUSTER: dev.mgeo.local
	- PROJECT: mgeo-dev

Give me the Adminpassword
supergeheim
[ok]	Creating configmap 'mgeo-dev'
[ok]	Creating secret 'mgeo-dev'
[ok]	Creating filesecret 'mgeo-dev-files'
[ok]	Creating Routes for 'mgeo-dev'

```

The process won't ask on CLI if you've set the defined key by an exported ENV variable. 
