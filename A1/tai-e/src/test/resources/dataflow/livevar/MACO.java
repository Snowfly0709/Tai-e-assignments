package stu.xjtu.Xiong;

import java.io.*;
import java.util.Date;
import java.util.HashMap;


public class MACO
{
    //方法引入
    public int[][] serviceInput() throws UnsupportedEncodingException {
        InputStream inputStream = this.getClass().getResourceAsStream("service.txt");
        InputStreamReader read = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = null;
        int[][] service = new int[10][80];
        String temp = null;
        int column = 0;
        int row =0;
        try{
            bufferedReader = new BufferedReader(read);
            while ((temp = bufferedReader.readLine()) != null) {
                service[row][column] = Integer.parseInt(temp);
                column = column + 1;
                if(column == 80){
                    column = 0;
                    row = row + 1;
                }
                if(row == 10){
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return service;
    }

    public int[][] initMachineMem() throws UnsupportedEncodingException {
        InputStream inputStream = this.getClass().getResourceAsStream("firstMem.txt");
        InputStreamReader read = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = null;
        String temp = null;
        int i = 0;
        int[][] macMem = new int[1][10];
        try {
            bufferedReader = new BufferedReader(read);
            while ((temp = bufferedReader.readLine()) != null) {
                macMem[0][i] = Integer.parseInt(temp);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macMem;
    }

    public int[][] initServiceMem() throws UnsupportedEncodingException {
        InputStream inputStream = this.getClass().getResourceAsStream("sevMem.txt");
        InputStreamReader read = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = null;
        String temp = null;
        int i = 0;
        int[][] sevMem = new int[1][80];
        try {
            bufferedReader = new BufferedReader(read);
            while ((temp = bufferedReader.readLine()) != null) {
                sevMem[0][i] = Integer.parseInt(temp);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sevMem;
    }

    public int[][] transform(int[][] Matrix) throws UnsupportedEncodingException {
        int[] machineServiceNum = new int[Matrix.length];
        for (int i = 0; i < Matrix.length; i++) {
            for (int i1 = 0; i1 < Matrix[i].length; i1++) {
                machineServiceNum[i] = machineServiceNum[i] + Matrix[i][i1];
            }
        }
        int maxCount = 0;
        for (int j = 0; j < machineServiceNum.length; j++) {
            if (machineServiceNum[j] > maxCount) {
                maxCount = machineServiceNum[j];
            }
        }
        int[][] bestMatrix = new int[Matrix.length][maxCount];
        //将service放入数量矩阵内
        for (int i = 0; i < Matrix.length; i++) {
            int index = 0;
            for (int i1 = 0; i1 < Matrix[i].length; i1++) {
                while (Matrix[i][i1] != 0) {
                    int flag = 0;
                    while (flag == 0) {
                        if (bestMatrix[i][index] != 0) {
                            index = index + 1;
                        } else {
                            flag = 1;
                            bestMatrix[i][index] = i1 + 1;
                            Matrix[i][i1] = Matrix[i][i1] - 1;
                        }
                    }
                    if (Matrix[i][i1] == 0) {
                        break;
                    }
                }
            }
        }
        return bestMatrix;
    }

    public HashMap<String,Integer> outService() throws UnsupportedEncodingException {
        InputStream inputStream = this.getClass().getResourceAsStream("out.txt");
        InputStreamReader read = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = null;
        HashMap<String, Integer> out_serviceFlow = new HashMap<String,Integer>();
        String temp = null;
        String[] temp1;
        String hashKey;
        int hashValue;
        try {
            bufferedReader = new BufferedReader(read);
            while((temp = bufferedReader.readLine())!=null){
                temp1 = temp.split(" ");
                hashKey = temp1[0] + " " + temp1[1];
                hashValue = Integer.parseInt(temp1[2]);
                out_serviceFlow.put(hashKey,hashValue);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return out_serviceFlow;
    }

    //in流量Hashmap
    public HashMap<String,Integer> inService() throws UnsupportedEncodingException {
        InputStream inputStream = this.getClass().getResourceAsStream("in.txt");
        InputStreamReader read = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = null;
        HashMap<String, Integer> in_serviceFlow = new HashMap<String,Integer>();
        String temp = null;
        String[] temp1;
        String hashKey;
        int hashValue;
        try {
            bufferedReader = new BufferedReader(read);
            while((temp = bufferedReader.readLine())!=null){
                temp1 = temp.split(" ");
                hashKey = temp1[0] + " " + temp1[1];
                hashValue = Integer.parseInt(temp1[2]);
                in_serviceFlow.put(hashKey,hashValue);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return in_serviceFlow;
    }

    //比较某台物理机内某service组in流量与out流量
    public static int comparison(int out, int in, int outAmount, int inAmount){
        int outFlowTotal = out*outAmount;
        int inFlowTotal = in*inAmount;
        return Math.min(outFlowTotal,inFlowTotal);
    }

    //对于每台物理机内部的service组计算traffic
    public int totalServiceFlow(int[][] service) throws UnsupportedEncodingException {
        int totalFlow = 0;
        HashMap<String,Integer> out_serviceFlow = outService();
        HashMap<String,Integer> in_serviceFlow = inService();
        for(int i = 0;i < service.length;i++){
            for(int j = 0; j< service[i].length-1;j++){
                for(int k = j+1;k < service[i].length;k++) {
                    String service_pair = String.valueOf(j + 1) + " " + String.valueOf(k + 1);
                    int inFlow = in_serviceFlow.get(service_pair);
                    int outFlow = out_serviceFlow.get(service_pair);
                    int result = comparison(outFlow, inFlow, service[i][j], service[i][k]);
                    totalFlow = totalFlow + result;
                }
            }
        }
        return totalFlow;
    }
    //蚁群算法
    public static void main (String[] args) throws UnsupportedEncodingException, FileNotFoundException {
        MACO maco = new MACO();
        //初始化service
        int[][] service = new int[10][80];
        service = maco.serviceInput();
        int[][] stable = maco.serviceInput();
        //初始化物理机内存
        int[][] machineMem = new int[1][10];
        machineMem = maco.initMachineMem();
        //初始化service内存
        int[][] serviceMem = new int[1][80];
        serviceMem = maco.initServiceMem();
        //flag表示物理机内是否有service
        int[] flag = new int[10];
        //初始化迭代次数
        int iterNum = 600;
        //初始化信息素
        double[][] pheromone = new double[1][10];
        for(int i = 0;i < 10;i++){
            pheromone[0][i] = 1;
        }
        //流量系数3，信息素系数3，信息素挥发系数0.7
        double alpha = 3;
        double beta = 1;
        double rho = 0.2;
        int num = 0;
        //蚁群算法主循环
        for(int i = 0;i < iterNum;i++){
            System.out.println(new Date());
            num = num + 1;
            //System.out.println("第" + i + "次迭代");
            //计算当前物理机是否为空，若service每一种都为0，则flag为1，否则为0
            for(int j = 0;j<service.length;j++) {
                int count = 0;
                for (int k = 0; k < service[j].length; k++) {
                    if (service[j][k] == 0) {
                        count = count + 1;
                    }
                }
                if (count == 80) {
                    flag[j] = 1;
                }
            }
            //初始化蚂蚁数量为flag = 0的物理机数量
            int antNum = 0;
            for(int j = 0;j < flag.length;j++){
                if(flag[j] == 0){
                    antNum = antNum + 1;
                }
            }
            //初始化蚂蚁位置
            int m = 0;
            int[] antPosition = new int[antNum];
            for(int j = 0;j < flag.length;j++){
                if(flag[j] == 0){
                    antPosition[m] = j;
                    m = m + 1;
                }
            }
            //生成0-80的随机数
            //将顺序蚂蚁看做顺序物理机上的随机一个service
            int[] Sant = new int[antNum];
            for(int j = 0;j < antNum;j++){
                while(true){
                    int temp = (int)(Math.random()*80);
                    if(service[antPosition[j]][temp] != 0){
                        Sant[j] = temp;
                        break;
                    }
                }
            }
            //对于每一只蚂蚁
            for(int ant = 0;ant < antNum; ant++){
                //System.out.println("——————————————————————————————————————————————");
                //计算流量
                int flow = maco.totalServiceFlow(service);
                double[] chance = new double[10];
                //计算迁移到其他物理机后流量的变化
                for(int machine = 0;machine < machineMem[0].length;machine++){
                    //判断能否迁入
                    if(flag[machine] == 0 && machineMem[0][machine] >= serviceMem[0][Sant[ant]]){
                        //计算流量变化
                        service[antPosition[ant]][Sant[ant]] -= 1;
                        service[machine][Sant[ant]] += 1;
                        int flowChange = maco.totalServiceFlow(service) - flow;
                        if(flowChange <= 0){
                            flowChange = 1;
                        }
                        //计算概率
                        chance[machine] = Math.pow(flowChange,alpha)*Math.pow(pheromone[0][machine],beta);
                        //恢复service
                        service[antPosition[ant]][Sant[ant]] += 1;
                        service[machine][Sant[ant]] -= 1;
                    }
                }
                //将概率归一化sigmoid
                double sum = 0;
                for(int k = 0;k < chance.length;k++){
                    sum += chance[k];
                }
                for(int k = 0;k < chance.length;k++){
                    chance[k] = chance[k]/sum;
                }
                //轮盘赌选择物理机
                double[] roulette = new double[10];
                if(chance[0] != 0){
                    roulette[0] = chance[0];
                }else{
                    roulette[0] = 0;
                }
                for(int k = 1;k < chance.length;k++){
                    roulette[k] = roulette[k-1] + chance[k];
                }
                double rand = Math.random();
                int machine = 0;
                for(int k = 0;k < roulette.length;k++){
                    if(rand < roulette[k]){
                        machine = k;
                        break;
                    }
                }
                //将当前需要放入的service放入
                service[antPosition[ant]][Sant[ant]] -= 1;
                service[machine][Sant[ant]] += 1;
                //更新信息素
                for(int k = 0;k < pheromone[0].length;k++) {
                    if(k == machine) {
                        pheromone[0][machine] = (1 - rho) * pheromone[0][machine] + chance[machine];
                    }else {
                        pheromone[0][k] = (1 - rho) * pheromone[0][k];
                    }
                }
                //打印当前选择
                System.out.println(num+"回合，第"+ant+"只蚂蚁，从第"+antPosition[ant]+"台离开，选择了第"+machine+"台物理机"+" ");
                //System.out.println("当前流量为："+maco.totalServiceFlow(service));
                //更新内存
                machineMem[0][machine] -= serviceMem[0][Sant[ant]];
                machineMem[0][antPosition[ant]] += serviceMem[0][Sant[ant]];
                /*
                //输出当前machineMem
                for(int k = 0;k < machineMem.length;k++){
                    for(int l = 0;l < machineMem[k].length;l++){
                        System.out.print(machineMem[k][l]+" ");
                    }
                    System.out.println();
                }
                //输出当前flag
                for(int k = 0;k < flag.length;k++){
                    System.out.print(flag[k]+" ");
                }
                //检查当前service的每一种数量之和是否与stable的每一种数量之和相等
                int count = 0;
                for(int k = 0;k < service[0].length;k++){
                    int sumup1 = 0;
                    int sumup2 = 0;
                    for(int l = 0;l < service.length;l++){
                        sumup1 = sumup1 + service[l][k];
                        sumup2 = sumup2 + stable[l][k];
                    }
                    if(sumup1 == sumup2){
                        count = count + 1;
                    }
                }
                if (count == 80) {
                    System.out.println("稳定");
                }else{
                    System.out.println("不稳定");
                }
                System.out.println("——————————————————————————————————————————————");
            */
            }
            //计算flag中1的个数
            int count = 0;
            for(int j = 0;j < flag.length;j++){
                if(flag[j] == 1){
                    count = count + 1;
                }
            }
            //输出当前service
            for(int k = 0;k < service.length;k++){
                System.out.print(maco.totalServiceFlow(service) +"、"+ count + "、");
                for(int l = 0;l < service[k].length;l++){
                    System.out.print(service[k][l]+" ");
                }
                System.out.println();
            }
            //将输出的全部内容存到txt文件中
            try {
                FileWriter fw = new FileWriter("maco.txt",true);
                fw.write(num+"、"+maco.totalServiceFlow(service) +"、"+ count);
                fw.write("\r\n");
                for(int k = 0;k < service.length;k++){
                    for(int l = 0;l < service[k].length;l++){
                        fw.write(service[k][l]+" ");
                    }
                    fw.write("\r\n");
                }
                fw.write("\r\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
