//实验一,第一版.2019.3.11.林木钏
//look_head对输入串进行扫描
//如果look_head扫描到终结符,则match该终结符
//函数match的作用在于对look_head的更新,以便于look_head扫描完整个算术表达式
//函数expr()表示输入的整个算术表达式
//将算术表达式由中缀表达式转换为后缀表达式 语法规则如下:
/*其中expr，rest为非终结符,term为终结符
 *expr->term rest
 *rest->+ term
 *			|- term
 *			|term
 *term->0|1|....|9
 */
//如果左递归的文法无法正确翻译的话,考虑右递归文法吧,
//将左递归转化为右递归的原因是为了避免翻译错误而不是为了避免无限递归，理由：执行语义动作是从左往右
//上面的意思是想象一下深度优先搜索一棵树,从左往右地后序遍历这颗树

//实验一,第二版.2019.3.13.张烁
//本次修改做了如下改进：
// 1.可以处理小数 2.引入取反操作
//3.根据实验要求“读取文件中的中缀表达式（每个表达式以分号结束，文件中可以有多个表达式并分号结束）”做出改进，
//     运行程序后输入txt文件的路径
//4.体现各操作符的优先级，后缀表达式是没有优先级的，但是中缀表达式有
//注意：本次改进将上个版本的lookhead当作向前搜索符的下标，并用一个字符变量ch来指示当前的向前搜索符
/*
    文法规则如下：
     *expr->term rest
     *rest->+ term|- term|ε
     *term->factor op
     * op-> * factor| / factor| ε
     *facror-> (expr) | num | ( -factor)
 */

//实验一,第三版.2019.3.15 郑森炯
//本次修改做了如下改进：
//增加了表达式出错的提示
// 其中 （1）缺少操作符号的错误由于表达式去掉了空格，所以有可能两个数合并了，判断不出错误
// 故增加了对数的判断，加入 int point对小数点进行判断，多余一个小数点的就是发生了错误 如11.11.22
// （2）判断数的格式是否错误  如22q 错误
// （3）缺少操作数（操作表达式的错误）判断  如： +5、*（1+4）缺少5、（1+4）
//生成表达式 ，这里只生成正确的表达式 Product
//开始时进行选择

//实验一,第四版.2019.3.26 张烁
//本次实现的功能：
//1.实现出错恢复
//能够判断的错误类型有三种：（1）缺少操作数，比如“  + 43”
//                        （2）操作数中有多个小数点，比如“1.2.3”
//                        （3）操作数中混入其他字符，比如“12p + 36”
//2.生成错误的表达式，并将生成的表达式写入文件，方便测试

import java.io.*;
import java.util.Scanner;
import java.util.Random;

// product
/**生成测试的算术表达式的工具类
 * 可以生成正确的算术表达式或者错误的算术表达式
 * @author 郑森炯  张烁
 * @version V2.0
 */
class Product{
    Random rand=new Random();
    String pexp="";
    int cur;
    /**
     * 该类生成表达式所调用的子函数
     */
    void expr()
    {
        int enumber=1;
        term();
        while (true) {
            cur = rand.nextInt(100)+1;
            if (cur > enumber * 30) {
                enumber++;
                if (cur > 50) {
                    pexp += "+";
                } else {
                    pexp += "-";
                }
                term();
            }else{
                break;
            }
        }
    }
    /**
     * 该类生成表达式所调用的子函数
     */
    void term()
    {
        int tnumber=1;
        factor();
        while (true) {
            cur = rand.nextInt(100)+1;
            if (cur > tnumber *40) {
                tnumber++;
                if (cur > 50) {
                    pexp += "*";
                } else {
                    pexp += "/";
                }
                factor();
            }else{
                break;
            }
        }
    }
    /**
     * 该类生成表达式所调用的子函数
     */
    void factor(){
        cur = rand.nextInt(100)+1;
        if(cur>50){
            pexp+="(";
            cur = rand.nextInt(100)+1;
            if(cur>50){
                expr();
            }else{
                pexp+="-";
                num();
            }
            pexp+=")";
        }else{
            num();
        }
    }
    /**
     * 该类生成表达式所调用的子函数
     */
    void num(){
        cur = rand.nextInt(100)+1;
        pexp+=cur;
        if(cur>50){
            pexp+=".";
            pexp+=rand.nextInt(100);
        }
    }
    /**
     * 生成正确的算术表达式
     * @return 返回一条正确的算术表达式
     */
    public String creat(){
        pexp="";
        expr();
        return pexp;
    }
    /**
     * 生成错误的算术表达式
     * 错误类型包括：1.缺少操作数；2.有多个小数点；3.混入其他不可识别的字符
     * @return 返回一条错误的算术表达式
     */
    public String creat_1(){
        pexp ="";
        expr();
        int choice =rand.nextInt(3)+1;  //前面已经说到，能够判断的错误的类型有三种，这里通过
        if(choice==1){                  //生成一个随机数来选择生成哪种类型的错误表达式
            for(int i=0;i<pexp.length();i++){
                char x= pexp.charAt(i);
                if(Character.isDigit(x))
                {
                    StringBuilder builder= new StringBuilder(pexp);
                    builder.setCharAt(i, ' ');
                    pexp= builder.toString();
                }
            }
        }
        else if(choice==2){
            for(int i=0;i<pexp.length();i++){
                char x= pexp.charAt(i);
                if(x=='.')
                {
                    StringBuilder builder= new StringBuilder(pexp);
                    builder.setCharAt(i-1, '.');
                    pexp= builder.toString();
                }
            }            
        }
        else if(choice==3){
            for(int i=0;i<pexp.length();i++){
                char x= pexp.charAt(i);
                if(Character.isDigit(x))
                {
                    StringBuilder builder= new StringBuilder(pexp);
                    builder.setCharAt(i+1, 'p');
                    pexp= builder.toString();
                    break;
                }
            }           
        }

        return pexp;
    }
}


// -------------->product

/**用于将中缀表达式转化为后缀表达式的工具类
 * 利用递归子程序法自顶向下分析算术表达式，在分析的过程中加入语义动作使中缀表达式转化为后缀表达式
 * @author 林木钏  张烁   郑森炯
 * @version 3.0
 */
class Praser{

    int lookhead; //向前搜索一个字符的下标
    char ch;        //向前搜索的字符
    String exp;     //当前正在处理的表达式
    int size;       //当前正在处理的表达式的长度
    String []infix;     //中缀表达式
    int point=0;
    boolean flag;  //引入flag来记录当前转化的这条表达式是否没有错误，是为了在转化时发现错误后
                //还能继续分析以达到出错恢复

    /**
     * 该类的构造函数
     * @param exps 用户接受一个string类型的数组（欲转化的中缀表达式）
     */
    public Praser(String []exps) throws IOException {
        infix =exps;
        // TODO Auto-generated constructor stub
    }

    /**
     * 进行转化所调用的子函数
     * @throws IOException
     */
    void expr() throws IOException
    {
        ch= exp.charAt(lookhead);
        term();
        while (true) {
            if(ch=='+')
            {
                match('+');term();
                if(flag==true)
                {
                    System.out.print('+');System.out.print(' ');
                }   
            }
            else if (ch=='-') {
                match('-');term();
                if(flag==true)
                {
                    System.out.print('-');System.out.print(' ');
                }
            }
            else if(ch=='E'){
                return ;
            }
            else
                return;                          
        }
    }

    /**
     * 进行转化所调用的子函数
     * @throws IOException
     */
    void term() throws IOException
    {
        factor();
        while(true){
            if(ch=='*'){
                match('*');factor();
                if(flag==true)
                {
                    System.out.print('*');System.out.print(' ');
                }
            }else if(ch=='/'){
                match('/');factor();
                if(flag==true)
                {
                    System.out.print('/');System.out.print(' ');
                }
            }else{
                return ;
            }
        }
    }

    /**
     * 进行转化所调用的子函数
     * @throws IOException
     */
    void factor() throws IOException {
        if(ch=='('){
            match('(');
            if(ch=='-'){        //这时要考虑括号内是另一个表达式还是取反操作
                match('-');
                if(flag==true)
                    System.out.print("( - ");
                factor();
                if(flag==true)
                    System.out.print(") ");
            }
            else
                expr();
            match(')');
        }else if(Character.isDigit(ch)){
            num();System.out.print(' ');
        }
        else{
            System.out.println("\n表达式出错！ 错误类型：缺少运算数字（或者运算表达式）或者混入无法识别的字符 错误位置:  "+(lookhead+1));
            flag= false;
            if(lookhead+1<size)
                ch=exp.charAt(++lookhead);
        }
    }

    /**
     * 进行转化所调用的子函数
     * @throws IOException
     */
    void num() throws IOException {
        while(true) {
            if (Character.isDigit(ch) || ch == '.') {
                if(ch=='.'){
                    point++;
                    if(point>1){
                        System.out.println("\n表达式出错！ 错误类型：操作数表达出错（小数点数目>1） 错误位置:  "+(lookhead+1));
                        flag= false;
                       //发现错误后不停止，继续分析
                       if(lookhead+1<size)
                            ch=exp.charAt(++lookhead);
                    }
                }
                if(flag==true)
                    System.out.print(ch);
                match(ch);
            } else if(ch=='*'||ch=='/'||ch=='+'||ch=='-'||ch==')'||lookhead==size-1){
                break;
            }else{
                System.out.println("\n表达式出错！ 错误类型：操作数表达出错（混入不支持的字符） 错误位置:  "+(lookhead+1));
                flag= false;
                if(lookhead+1<size)
                    ch=exp.charAt(++lookhead);
            }
        }
        point=0;
    }

    /**
     * 进行转化所调用的子函数
     * @throws IOException
     */
    void match(char t) throws IOException
    {
        if(ch==t&&lookhead<size-1) ch=exp.charAt(++lookhead);
        else if(ch==t&&lookhead==size-1){   //运行到这里就是处理完一个表达式
            ch='E';     //由于已经遍历完整个中缀表达式了，现在把向前搜索符置为‘E’表示End
            return;
        }
        else {
            /*System.out.println(ch);
            System.out.println(t);
            throw new Error("syntax error");*/
            return;
        }
    }

    /**
     * 中缀表达式转化为后缀表达式的主控函数,转换的结果输出到控制台
     */
    public void inToPost() throws IOException {
        for (int i = 0; i < infix.length; i++) {
            System.out.print(infix[i]+":    ");
            lookhead = 0;
            flag= true;
            exp = infix[i];
            size = exp.length();
            expr();
            System.out.println();
        }
    }
}
class Postfix {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("请输入想进行的操作：****1 生成表达式  2 中缀表达式转换为后缀表达式****");
        Scanner opscanner = new Scanner(System.in);
        String op=null;
        if(opscanner.hasNext()) {
            op = opscanner.next();
        }

    
        if(op.equals("1")){
            System.out.println("请输入想进行的操作：****1 生成正确表达式  2 生成错误表达式****");
            Scanner scanner = new Scanner(System.in);
            int choice=0;
            if(scanner.hasNext()){
                choice= scanner.nextInt();
            }
            
            if(!(choice==1||choice==2)){
                System.out.println("输入有误！");
                System.exit(0);
            }
          
            System.out.println("请输入想生成的表达式的数量：");
            Scanner scanner_1= new Scanner(System.in);
            int num= 0;
            if(scanner_1.hasNext()){
                num= scanner_1.nextInt();
            }

            //生成表达式并写入文件
            Product product=new Product();
            String savePath=System.getProperty("user.dir");//生成的表达式存储存储在程序运行的路径下
            try{
                File file= new File(savePath+"\\generated_exprs.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);//true表示是否追加
                BufferedWriter bw = new BufferedWriter(fw);

                for(int i=0;i<num;i++)
                {
                    String expr= null;
                    if(choice==1)
                        expr= product.creat()+";";
                    else
                        expr= product.creat_1()+";";
                    bw.write(expr);
                }
                bw.close();
                System.out.println("生成表达式成功！位置："+savePath+"\\generated_exprs.txt");
            }catch(IOException e){
                e.printStackTrace();
            }


        }else if(op.equals("2")){
           System.out.println("请输入文件的路径（文件中每个表达式以分号结束，文件中可以有多个表达式并分号结束）：");
            Scanner scanner = new Scanner(System.in);
            String path =null;  //文件路径
            if(scanner.hasNext()) {
                path = scanner.next();
            }
            scanner.close();
        
           // Stirng path="C:\\Users\\sure\\Desktop\\study\\大三下\\编译原理项目19\\实验一\\generated_exprs.txt";
            String content =""; //文件中的内容
            try{
                Scanner scan = new Scanner(new BufferedReader(new FileReader(path)));
                while(scan.hasNext()){
                    content+= scan.next();
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String []exps = content.split(";");

            // TODO Auto-generated method stub
            Praser parse=new Praser(exps);
            parse.inToPost();
            System.out.print('\n');
        }else{
            System.out.println("输入错误");
        }
    }
}