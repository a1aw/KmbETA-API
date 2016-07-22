#!/bin/sh
cp KmbETA-API-$projectversion-$TRAVIS_BRANCH-B$TRAVIS_BUILD_NUMBER.zip KmbETA-API-$projectversion-latest.zip
ls -l
scp -v -i ~/.ssh/id_rsa KmbETA-API-$projectversion-latest.zip mob41,kmbeta-api@frs.sourceforge.net:/home/frs/project/k/km/kmbeta-api
