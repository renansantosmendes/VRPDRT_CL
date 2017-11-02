#!/bin/bash 
#SBATCH --qos=part3d
#SBATCH --partition=large
module load jdk8_32
java -jar r050n12tw10k11s.jar