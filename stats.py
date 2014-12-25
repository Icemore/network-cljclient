import subprocess

RUN_STRING = "java"
RUN_PARAMS = "-ea -cp target\\iterpreter-client-1.0-SNAPSHOT-jar-with-dependencies.jar ru.spbau.networks.client.Main".split(' ')
HOST = "192.168.1.47"
PORT = 2007

def run(clientsCnt, reqestLen):
    process = subprocess.Popen([RUN_STRING] + RUN_PARAMS + [HOST, str(PORT), str(clientsCnt), str(reqestLen)], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = process.communicate()
    
    res = float(out)

    if len(err) == 0:
        return res
    else:
        return "N/A"
 
 
with open("res_concat_data.txt", "wt", 0) as f:
    for i in xrange(1, 11):
        data_size = i**4
        cur = run(50, data_size)
        f.write("%s: %s\n" % (data_size, cur))
        print "%s: %s\n" % (data_size, cur)