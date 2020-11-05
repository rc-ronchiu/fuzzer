# Purpose
In this project, I implemented a fuzzer with a variety of techniques.
Generation based fuzzing, for example, was the first technique that
I used and it achieved around 80% of coverage score. After that, I
added mutation based fuzzing technique into the fuzzer, which 
improved the coveage score and it went up to around 86%. Finally, I
added modified random fuzzing technique so that the coverage score
reached 92%.

# Structure of this repository

* src/original/: -  where the code for the original application lives
* src/vuln-1 -- src/vuln-5 - where your vulnerable versions will live
* poc/:        -  where your PoCs will live
* fuzzer/:     -  where your fuzzer will live
* bin/:        -  where your compiled programs will live
* tests/:      -  where your generated tests will live

Pre-Included Scripts:

* Makefile         - makefile for building the C implementation etc.
* get_coverage.sh  - script to generate coverage reports
* run_fuzzer.sh    - script for running your fuzzer to generate inputs 
* run_tests.sh     - script for running your generated tests against compiled programs 

Vulnerable Versions (you should put your security vulnerabilities in here):

* src/vuln-1/dc.c -- src/vuln-5/dc.c

Proofs of Concept (PoCs that you should provide for each vulnerability):

* poc/vuln-1.poc -- poc/vuln-5.poc

