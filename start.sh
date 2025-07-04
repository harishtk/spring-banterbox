#!/bin/bash
docker-compose \
  -f Banterbox/docker-compose.yml \
  -f db-info/app-db/docker-compose.yml \
  up --build -d
