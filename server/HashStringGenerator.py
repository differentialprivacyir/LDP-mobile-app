import random

N = 32000 #number of words
n = 10 #lenght of string
len_min = 10
len_max = 30
f = open("random-hash-strings.txt", "w")

for i in range(N):

    l = random.randint(len_min,len_max)
    str = ""
    for j in range(l):
        r = random.randint(65,116)
        if r > 90:
            r+=6

        str+=chr(r)

    f.write(str+"\n")

f.close()




