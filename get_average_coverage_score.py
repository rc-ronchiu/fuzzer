import time
import sys
import os

if len(sys.argv) == 1:
	print("-Run this script by")
	print("python " + sys.argv[0] + " EXECUTION_TIMES NUMBER_OF_INSTRUCTION_IN_EACH_CORPUS NUMBER_OF_GENERATION_BASED_CORPUS\n")
	print("-Example:")
	print("python " + sys.argv[0] + " 10 10000 100")
	exit()

EXECUTION_TIMES = int(sys.argv[1])

timestr = time.strftime("%Y%m%d-%H%M%S")

regions = 0
missed_regions = 0
region_coverage = 0.0

functions = 0
missed_functions = 0
functions_coverage = 0.0

lines = 0
missed_lines = 0
lines_coverage = 0.0
	
for i in range(EXECUTION_TIMES):
	print("\n---------------")
	print("#" + str(i + 1) + " execution(s)")
	print("---------------\n")

	os.popen('sh run_fuzzer.sh')
	output = os.popen('sh get_coverage.sh tests/').read().splitlines()[-1].split()

	regions = int(output[1]) + regions
	missed_regions = int(output[2]) + missed_regions

	functions = int(output[4]) + functions
	missed_functions = int(output[5]) + missed_functions

	lines = int(output[7]) + lines
	missed_lines = int(output[8]) + missed_lines

os.chdir('average_coverage_score')
with open('average_coverage_score_' + timestr + '.txt', 'w') as f:
	f.write("Total execution times: " + str(EXECUTION_TIMES) + "\n")
	
	if len(sys.argv) == 4:
		f.write("Number of instructions in each corpus: " + sys.argv[2] + "\n")
		f.write("Number of generation-based corpus: " + sys.argv[3] + "\n")

	f.write("\n")
	f.write("Total regions: " + str(regions) + "\n")
	f.write("Total missed regions: " + str(missed_regions) + "\n")
	f.write("Average region coverage: " + "{:.2%}".format((regions - missed_regions) / float(regions)) + "\n")		

	f.write("Total functions: " + str(functions) + "\n")
	f.write("Total missed functions: " + str(missed_functions) + "\n")
	f.write("Average function coverage: " + "{:.2%}".format((functions - missed_functions) / float(functions)) + "\n")

	f.write("Total lines: " + str(lines) + "\n")
	f.write("Total missed lines: " + str(missed_lines) + "\n")
	f.write("Average line coverage: " + "{:.2%}".format((lines - missed_lines) / float(lines)) + "\n")

print("Average region coverage: " + "{:.2%}".format((regions - missed_regions) / float(regions)) + "\n")
print("Average function coverage: " + "{:.2%}".format((functions - missed_functions) / float(functions)) + "\n")
print("Average line coverage: " + "{:.2%}".format((lines - missed_lines) / float(lines)) + "\n")

os.chdir('..')
os.popen('rm tests/fuzz*')
