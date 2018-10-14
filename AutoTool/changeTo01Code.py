import re
def opToStr(ins):
    op=dic[ins[0]]
    num1=int(op[0])
    num2=int(op[1])
    high=['0','0','0']
    low=['0','0','0']
    for i in range(3):
        a=num1%2
        num1=int(num1/2)
        b=num2%2
        num2=int(num2/2)
        high[2-i]=str(a)
        low[2-i]=str(b)
    str1=''
    opArray=high+low
    str1=str1+''.join(opArray)
    num=[]
    if ins[0]!= 'SRC' and ins[0]!='RRC':
        for i in range(1,5):
            num.append(int(re.findall("\d+",ins[i])[0]))
        immed=[0,0,0,0,0]
        for i in range(5):
            a = num[3] % 2
            num[3] = int(num[3] / 2)
            immed[4 - i] = str(a)
        IR=[0,0]
        IX=[0,0]
        for i in range(2):
            a = num[0] % 2
            num[0] = int(num[0] / 2)
            IR[1 - i] = str(a)
            b = num[1] % 2
            num[1] = int(num[1] / 2)
            IX[1 - i] = str(b)
        content=IR+IX+[str(num[2])]+immed
        str1=str1+''.join(content)
    else:
        for i in range(1,5):
            num.append(int(re.findall("\d+",ins[i])[0]))
        immed=[0,0,0,0]
        for i in range(4):
            a = num[3] % 2
            num[3] = int(num[3] / 2)
            immed[3 - i] = str(a)
        IR = [0, 0]
        for i in range(2):
            a = num[0] % 2
            num[0] = int(num[0] / 2)
            IR[1 - i] = str(a)
        content=IR+[str(num[1])]+[str(num[2])]+['00']+immed
        str1 = str1 + ''.join(content)

    return str1





file=open('example.txt')
inst=[]
addInfo=[]
str1=file.readline()
while str1:
    arr=str1.split('//')
    if len(arr)>1:
        addInfo.append(arr[1])
    else:
        addInfo.append('\n')
    arr2=arr[0].split(' ')
    arr2=arr2[:5]
    arr2=[arr2[i].strip('\n') for i in range(5)]
    inst.append(arr2)
    str1 = file.readline()

dic={'LDR':'01','STR':'02','LDA':'03','LDX':'41','STX':'42','JZ':'10','JNE':'11','JCC':'12',
     'JMA':'13','JSR':'14','RFS':'15','SOB':'16','JGE':'17','AMR':'04','SMR':'05','AIR':'06','SIR':'07',
     'MLT':'20','DVD':'21','TRR':'22','AND':'23','ORR':'24','NOT':'25','SRC':'31','RRC':'32',
     'IN':'61','OUT':'62','CHK':'63'}

newfile=open('reExample.txt','w')
for i in range(len(inst)):
    content=opToStr(inst[i])+'     //['+str(i+1000)+']'+addInfo[i]
    newfile.writelines(content)

newfile.close()
file.close()





