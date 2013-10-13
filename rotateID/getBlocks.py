import os



#os.path.abspath("")+"/blocks"+"/25.txt"

def readFile(path):
    f = file(path,"r")

    block = {}
    materials = []
    for line in f:
        sp = line[:-1].split(":")
        x = sp[0]
        y = sp[1]
        z = sp[2]
        name = x+z
        if not name in ["-75","-86","-77","-66"]:
            continue
        material = sp[3]
        dataValue = sp[4]
        block[name] = dataValue
        if not material in materials:
            materials.append(material)
        
    f.close()

    # check if material is identical
    if len(block) == 4 and len(materials) == 1:
        return materials,block
    return False,False


materials = {}
for path in os.listdir(os.path.abspath("")+"/blocks"):
    m,b = readFile(os.path.abspath("")+"/blocks/"+path)
    if not m:
        print "error:", path
    m = m[0]
    if m == "1" or m == "0":
        continue
    if not m in materials:
        materials[m] = []
    materials[m].append(b)



for m in materials:
	print m
	for b in materials[m]:
		print b.values()


# missing stairs, trapped chest
