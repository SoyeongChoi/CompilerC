import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniGoPrintListener extends MiniGoBaseListener {
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
	private boolean for_loop = false;//조건문이 그냥 binary operation인 경우
	private boolean for_loop2 = false;//조건문이 ; ; 인 경우
	private boolean return_val = false;//return
	private boolean check = false;//if나 for문에 binary 오는 경우 { 처리
	private int depth = 0;//중첩문 판단
	private boolean else_stmt = false;//if문에 else가 붙는 경우
	private boolean print = false;//fmt.print 처리
	private boolean braket = false;//괄호가 있는 경우 (a + b)처리
	@Override
	public void enterDecl(MiniGoParser.DeclContext ctx) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ctx.getChildCount(); i++) {
			sb.append(newTexts.get((ctx.getChild(i))));
		}
		newTexts.put(ctx, sb.toString());
		
	}

	@Override
	public void exitDecl(MiniGoParser.DeclContext ctx) {
	}

	@Override
	public void enterFun_decl(MiniGoParser.Fun_declContext ctx) {
		System.out.print(ctx.getChild(0).getText()+" ");
		System.out.print(ctx.getChild(1).getText());//처음 function에 들어갈 때 func하고 이름 출력
		depth++;//depth증가
	}

	@Override
	public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
		System.out.println("}");//func에서 빠져나올때 닫는 괄호 출력
		depth--;//depth 감소
	}

	@Override
	public void enterIf_stmt(MiniGoParser.If_stmtContext ctx) {
		System.out.print(ctx.getChild(0).getText()+" ");//if 출력
		depth++;//depth 증가
		check = true;
		if(ctx.getChildCount()>3){//else가 있다는 것이므로
			else_stmt = true;//else_stmt처리
		}
	}

	@Override
	public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
		for(int i =0; i<depth-1; i++){
			System.out.print("....");
		}
		System.out.print("}\n");//닫는괄호 
		
		depth--;//depth감소
	
	}

	@Override
	public void enterProgram(MiniGoParser.ProgramContext ctx) {
		System.out.println();
	}

	@Override
	public void exitProgram(MiniGoParser.ProgramContext ctx) 
	{
	}

	@Override
	public void enterExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
	
	}

	@Override
	public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
	//System.out.println();
	}

	@Override
	public void enterLoop_expr(MiniGoParser.Loop_exprContext ctx) {
		for_loop2 = true;
		System.out.print(ctx.getChild(0).getChild(0).getText()+" "+ctx.getChild(0).getChild(1).getText()+" "+ctx.getChild(0).getChild(2).getText()+";");
	}//loop_expr을 들어오는 경우는 for ; ; 인 경우밖에 없으므로 처음 초기화해주는 값을 바로 여기서 출력시킴
	
	@Override
	public void exitLoop_expr(MiniGoParser.Loop_exprContext ctx) {
		System.out.print(ctx.getChild(ctx.getChildCount()-2).getText());
		System.out.print(ctx.getChild(ctx.getChildCount()-1)+"{\n");//loop를 빠져나올때 바로 {를 찍어줌
		check = true;
		for_loop2 = false;
	}
	
	@Override
	public void enterCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
		if(!check){//만약 {를 다른곳에서 출력을 안시켰다면( if나 for같은)
			if(else_stmt){//else문이라면
				System.out.print("}else ");//else출력해주고
				depth++;//depth를 증가시킴
				else_stmt = false;//else 판단문 삭제
			}
			System.out.println(ctx.getChild(0).getText());//{출력
		}else if(check){//check = true라면
			check = false;	//아무것도 출력안시키고 false로 변환시켜줌
		}		
	}

	@Override
	public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
		if(else_stmt) depth--;//else라면 depth를 하나 줄여줌(어차피  'else'출력하고 depth원상복귀시켜주기 때문에 상관 X )
	}
	@Override
	public void enterArgs(MiniGoParser.ArgsContext ctx) {
		if(ctx.getChildCount()==0)//()인 경우
		//	System.out.println("()");
			System.out.println();
		if(ctx.getChildCount()>0 && !print){//()가 아닌 경우			
			System.out.println(ctx.getChild(0).getText());
			if(ctx.getChildCount()>1){//(a)가 아니라 (i int이런게 있는 경우)
							
				System.out.println(ctx.getChild(1).getText()+ctx.getChild(2).getText());
				
			}
		}
		if(print){//fmt.print인 경우
			print = false;
			System.out.println();
		}
	}

	@Override
	public void exitArgs(MiniGoParser.ArgsContext ctx) {
	
	}

	@Override
	public void enterLocal_decl(MiniGoParser.Local_declContext ctx) {
		for(int i = 0 ; i < depth; i++){//local이므로 중첩이 얼마나 되어있는지 판단해 ....을 찍어줌
			System.out.print("....");
		}
		if(ctx.getChildCount() == 6){//var ans [15] int 이런 걸 가졌을 때
			System.out.print(ctx.getChild(0).getText()+" ");
			System.out.print(ctx.getChild(1).getText()+" ");
			System.out.print(ctx.getChild(2).getText());
			System.out.print(ctx.getChild(3).getText());
			System.out.print(ctx.getChild(4).getText()+" ");
			System.out.print(ctx.getChild(5).getText()+" ");
		}else{//나머지 경우
			for(int i =0; i<ctx.getChildCount();i++){
				System.out.print(ctx.getChild(i).getText()+" ");
			}
		}
		System.out.println();

	}

	@Override
	public void exitLocal_decl(MiniGoParser.Local_declContext ctx) {
	}

	
	public void enterType_spec(MiniGoParser.Type_specContext ctx) {
		
	}

	@Override
	public void exitType_spec(MiniGoParser.Type_specContext ctx) {
	
	}

	@Override
	public void enterParam(MiniGoParser.ParamContext ctx) {
		if(ctx.getChildCount()>1){	//param이 들어올 때 param이 i int인 경우		
			System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText());
		}else{//param이 들어올 때 처음에 하나인 경우는 x, i int인 경우이므로 ,을 출력시킴
			System.out.print(ctx.getChild(0).getText()+", ");
		}
		//newTexts.put(ctx, s1+" "+s2);
	}

	@Override
	public void exitParam(MiniGoParser.ParamContext ctx) {
		
	}
	@Override 
	public void enterParams(MiniGoParser.ParamsContext ctx) {//param이 들어올 때
		System.out.print("(");//소괄호  출력
			StringBuffer s1 = new StringBuffer();
		for(int i = 0; i < ctx.getChildCount(); i++) {
			if(i % 2 != 0) {
				s1.append(", ");
			}else {
				s1 = s1.append(newTexts.get((ctx.getChild(i))));
			}
		}
		newTexts.put(ctx, s1.toString());
	}
	@Override public void exitParams( MiniGoParser.ParamsContext ctx) {
		System.out.print(")");//param나갈 때 소괄호 출력
		
	}
	
	@Override
	public void enterFor_stmt(MiniGoParser.For_stmtContext ctx) {
		System.out.print(ctx.getChild(0)+" ");//'for' 출력
		depth++;//depth증가
		if(ctx.getChild(1).getChildCount() == 6){// for ; ; 인 경우
			for_loop2 = true;
		}else{	//for binary인 경우
			for_loop = true;
		}
		//System.out.print(ctx.getChild(0).getText());
	}

	@Override
	public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
		for(int i =0; i<depth-1;i++){//빠져나갈때 ....을 찍어주고
			System.out.print("....");
		}
		depth--;//depth감소시킨 후
		System.out.print("}\n");//} 출력
//		System.out.println(ctx.getChild(0).getText());
	}
	@Override
	public void enterExpr(MiniGoParser.ExprContext ctx) {
		//System.out.println("CHECK"+ctx.getChildCount());
		String s1 = null, s2 = null, op = null;
		if (isBinaryOperation(ctx)) {//binary인 경우
			
			s1 = newTexts.get(ctx.expr(0));
			s2 = newTexts.get(ctx.expr(1));
			op = ctx.getChild(1).getText();
			if(op.equals("=")&&ctx.getChild(2).getChildCount()>1){//binary를 판단할 때 = 이 나오는 경우도 판단하므로 제외시켜준다.

					System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" ");//앞에 두개만 출력
			}else if(return_val){//return인 경우
				return_val = false;//출력시키지않고 false로 변환시킴
			}else if(ctx.getChild(0).getText().equals("(")){//괄호를 포함하는 경우 ( x+y )가 들어올 때
				braket = true;
				System.out.print(ctx.getChild(0).getText());//맨 앞 괄호만 출력시켜줌(어차피 나머지는 다시 binary를 통해 x + y로 출력됨
			}
			else{
				if(check){//for나 if가 왔을 때 바로 괄호를 찍어줌
					System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+"{\n");
					
				}else{
					if(!for_loop&&!for_loop2){//둘다 해당하지 않으면	
						if(braket){//괄호가 있었으면 false로 변환시키고, )를 출력시켜주어 (x + y)의 형태로 바꿈
							System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+")\n");
							braket = false;
						}else{//아니라면							
							System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+"\n");//그냥 출력
						}
						
					}
					if(for_loop2){//for_loop2인 경우 ';'를 달고 출력시킴
						System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+";");
					}
					if(for_loop){//for_loop이면 그냥출력시킴
						System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText());
						
					}
				}
			
				newTexts.put(ctx, s1 + " " + op + " " + s2);
		
			}
			
		}else if(ctx.getChildCount() == 2){//++인 경우
			s1 = ctx.getChild(0).getText();
			op = ctx.getChild(1).getText();
			if(!s1.equals("++")){
				if(!s1.equals("--")){					
					System.out.println(s1+op);
				}
			}
			
			newTexts.put(ctx, s1 + op );
			
		}else if(ctx.getChildCount() == 1){//단순 expr
			s1 = ctx.getChild(0).getText();
		//	System.out.print(s1);
			
			newTexts.put(ctx, s1);
			
		}else if(ctx.getChildCount() == 4){//expr의 길이가 4인 것이 들어오는경우 그냥 출력시킴
			for(int i =0; i<ctx.getChildCount();i++){
				System.out.print(ctx.getChild(i).getText());
			}
		}else if(ctx.getChildCount() == 6){
			if(ctx.getChild(1).getText().equals(".")){//fmt.print인 경우
				print = true;
				for(int i = 0; i < ctx.getChildCount();i++){
					System.out.print(ctx.getChild(i).getText());
				}
			}else{//아닌 경우
				for(int i = 0; i < ctx.getChildCount();i++){
					System.out.print(ctx.getChild(i).getText());
				}
				System.out.println();
			}
			
		}
	}

	@Override
	public void exitExpr(MiniGoParser.ExprContext ctx) {
		
	}

	@Override
	public void enterVar_decl(MiniGoParser.Var_declContext ctx) {
		if(ctx.getChildCount() == 6){//전역변수로 var ans [15] int인 경우
			System.out.print(ctx.getChild(0).getText()+" ");
			System.out.print(ctx.getChild(1).getText()+" ");
			System.out.print(ctx.getChild(2).getText());
			System.out.print(ctx.getChild(3).getText());
			System.out.print(ctx.getChild(4).getText()+" ");
			System.out.print(ctx.getChild(5).getText()+" ");
		}else{//아닌 경우
			for(int i =0; i<ctx.getChildCount();i++){
				System.out.print(ctx.getChild(i).getText()+" ");
			}
		}
		System.out.println();
	}

	@Override
	public void exitVar_decl(MiniGoParser.Var_declContext ctx) {
	}

	@Override
	public void enterDec_spec(MiniGoParser.Dec_specContext ctx) {
	}

	@Override
	public void exitDec_spec(MiniGoParser.Dec_specContext ctx) {
	//	System.out.println(ctx.getChild(0).getText());
	}
	
	@Override
	public void enterStmt(MiniGoParser.StmtContext ctx) {	
		for(int i = 0;i<depth;i++){//stmt를 들어왔을 때
				if(!check){
					if(!for_loop){						
						System.out.print("....");//"...."을 출력시킴
					}
				}
		}
		if(for_loop){
			for_loop = false;
		}
			
		
		newTexts.put(ctx, ctx.getChild(0).getText());
		
	}

	@Override
	public void exitStmt(MiniGoParser.StmtContext ctx) {
		
	}

	@Override
	public void enterReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
		return_val = true;
		for(int i =0; i < ctx.getChildCount();i++){//return value를 출력시킴	
			System.out.print(ctx.getChild(i).getText()+" ");
		}

	}

	@Override
	public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
		System.out.println();
	}

	boolean isBinaryOperation(MiniGoParser.ExprContext ctx) {
		return ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr();
	}


}
