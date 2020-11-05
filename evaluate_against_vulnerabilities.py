import os

vuln_count = [0 for i in range(5)]
trigger_input_file = [[] for i in range(5)]
str_trigger_input_file = ["" for i in range(5)]

def evaluate(vuln_number, input_number):
	process = os.popen('./bin/vuln-' + str(vuln_number) + '/dc-san tests/fuzz-' + str(input_number) + '.txt 2>&1')
	output = process.read()
	if output != '':
		vuln_count[vuln_number - 1] += 1
		trigger_input_file[vuln_number - 1].append(input_number)
	process.close()

def run():
	for i in range(1, 6):
		for j in range(100):
			evaluate(i, j)
def show():
	print('#Number of inputs that triggers each vulnerability\n')
	for i in range(5):
		print('vuln-' + str(i + 1) + ': ' + str(vuln_count[i]))
		
	print('\n#Files that trigger each vulnerability:\n')
	for i in range(5):
		print('vuln-' + str(i + 1) + ':')
		filename = ''
		for j in trigger_input_file[i]:
			filename = filename + 'fuzz-' + str(j) + '.txt '
		print(filename)
		print('\n')

if __name__ == '__main__':
	run()
	show()
