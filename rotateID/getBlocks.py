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
        if not name in ["10","-10","0-1","01"]:
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


def toString(x):
    s = ""
    for i in x:
        s += str(i)
    return s

materials = {}
for path in os.listdir(os.path.abspath("")+"/blocks"):
    m,b = readFile(os.path.abspath("")+"/blocks/"+path)
    if not m:
        print "error:", path
        continue
    m = m[0]
    if m == "1" or m == "0":
        continue
    if not m in materials:
        materials[m] = []
    materials[m].append(b)




for m in materials:
    name = "b"+str(m)
    print ""
    print "BlockR "+name+" = new BlockR("+str(m)+");"
    for b in materials[m]:
        print name+".addRotationSet("+b["10"]+","+b["0-1"]+","+b["-10"]+","+b["01"]+");"
    print "blocks.put("+str(m)+","+name+");"



# missing stairs, trapped chest
