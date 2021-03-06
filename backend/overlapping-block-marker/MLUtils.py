
# Collection of utilities to manipulate
# files for the MLPClassifier


# Parse file in a list of the form 
# [ (category, text) ]
def parse_training_file(file_path):
    
    parsed = []

    training_file = open(file_path, 'r')
    for line in training_file:
        try:
            splitted = line.replace('\n','').split('\t')
            category = splitted[0]
            text = splitted[1].replace('\\n', '')
            parsed.append((category, text))
        except:
            continue 
    training_file.close()
    return parsed

# Produces list of integers, 
# ASCII values of the characters in given line
def format_line(line):
    
    formatted = []
    for char in line:
        formatted.append(ord(char))
    return formatted

# Pad strings
def pad(line, size):
    _line = line
    while len(_line) < size:
        _line += ' '
    return line
    
# Pad lists of floats
def pad_float(a_list, size):
    ORD_OF_SPACE = 32.0
    _list = a_list
    while len(_list) < size:
        _list.append(ORD_OF_SPACE)
    return _list
