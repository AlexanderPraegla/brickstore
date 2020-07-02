#!/bin/bash


echo "Building the UAA"
cd uaa
docker build --tag scg-demo-uaa .
cd ..

