# KmbETA-API [![Build Status](https://travis-ci.org/mob41/KmbETA-API.svg?branch=master)](https://travis-ci.org/mob41/KmbETA-API)

An API client for getting KMB bus's ETA.

JavaDoc: [http://mob41.github.io/KmbETA-API](http://mob41.github.io/KmbETA-API)

# No longer maintained

This project is abandoned and outdated because I have focused on developing it on GoToWhere platform. The latest API implementation is in JavaScript, and at the GoToWhere plugins repository: [gtwp-kmb](https://github.com/mob41/gotowhere-plugins/tree/master/repos/gtwp-kmb)

The current Java code current should still work. Unless there is serious changes that need to be made, I will not update the code because I don't have time.

Database builder and KMB database will still keep updated for using in GTW.

# License

Based on GNU GENERAL PUBLIC LICENSE Version 3.

>To "modify" a work means to copy from or adapt all or part of the work
>in a fashion requiring copyright permission, other than the making of an
>exact copy.  The resulting work is called a "modified version" of the
>earlier work or a work "based on" the earlier work.

I would like to clarify this point.

# Changelog

```1.0.0-SNAPSHOT``` Changelog:
 - Improved database structure
   - Changing to JSON format, more web friendly
   - Separating classes in API
 - Offline database is no longer required. By default, fetch the [pre-built database](https://github.com/mob41/KmbETA-DB) from the web
 - Support for web pre-built database. See [here](https://github.com/mob41/KmbETA-DB). (I call it "static database", but web)
 - Support for fetching database information directly (I call it "non-static database")
 - Some typo on function names
 - Improved some JavaDoc

# Tutorial

Check out the wiki [Quick Start](https://github.com/mob41/KmbETA-API/wiki/Quick-Start).

Offline database is <b>not required</b> since ```1.0.0-SNAPSHOT```. By default, the API will download the web DB from [here](https://github.com/mob41/KmbETA-DB) or [here](https://db.kmbeta.ml) on each launch.

Offline database can also be used by specifying a parameter to ```ArrivalManager```. (See the [wiki](https://github.com/mob41/KmbETA-API/wiki/Quick-Start)) It is also available to be [built](https://github.com/mob41/KmbETA-API/wiki/How-to-use-the-DBBuilder-(Auto-Mode)) using the [DB-Builder](https://github.com/mob41/KmbETA-DBBuilder) and downloaded from the [repository](https://github.com/mob41/KmbETA-DB) or the [website](https://db.kmbeta.ml/). (They are actually the same) But, now, the API still cannot built 