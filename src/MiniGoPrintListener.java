import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniGoPrintListener extends MiniGoBaseListener {
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
	private boolean for_loop = false;//���ǹ��� �׳� binary operation�� ���
	private boolean for_loop2 = false;//���ǹ��� ; ; �� ���
	private boolean return_val = false;//return
	private boolean check = false;//if�� for���� binary ���� ��� { ó��
	private int depth = 0;//��ø�� �Ǵ�
	private boolean else_stmt = false;//if���� else�� �ٴ� ���
	private boolean print = false;//fmt.print ó��
	private boolean braket = false;//��ȣ�� �ִ� ��� (a + b)ó��
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
		System.out.print(ctx.getChild(1).getText());//ó�� function�� �� �� func�ϰ� �̸� ���
		depth++;//depth����
	}

	@Override
	public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
		System.out.println("}");//func���� �������ö� �ݴ� ��ȣ ���
		depth--;//depth ����
	}

	@Override
	public void enterIf_stmt(MiniGoParser.If_stmtContext ctx) {
		System.out.print(ctx.getChild(0).getText()+" ");//if ���
		depth++;//depth ����
		check = true;
		if(ctx.getChildCount()>3){//else�� �ִٴ� ���̹Ƿ�
			else_stmt = true;//else_stmtó��
		}
	}

	@Override
	public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
		for(int i =0; i<depth-1; i++){
			System.out.print("....");
		}
		System.out.print("}\n");//�ݴ°�ȣ 
		
		depth--;//depth����
	
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
	}//loop_expr�� ������ ���� for ; ; �� ���ۿ� �����Ƿ� ó�� �ʱ�ȭ���ִ� ���� �ٷ� ���⼭ ��½�Ŵ
	
	@Override
	public void exitLoop_expr(MiniGoParser.Loop_exprContext ctx) {
		System.out.print(ctx.getChild(ctx.getChildCount()-2).getText());
		System.out.print(ctx.getChild(ctx.getChildCount()-1)+"{\n");//loop�� �������ö� �ٷ� {�� �����
		check = true;
		for_loop2 = false;
	}
	
	@Override
	public void enterCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
		if(!check){//���� {�� �ٸ������� ����� �Ƚ��״ٸ�( if�� for����)
			if(else_stmt){//else���̶��
				System.out.print("}else ");//else������ְ�
				depth++;//depth�� ������Ŵ
				else_stmt = false;//else �Ǵܹ� ����
			}
			System.out.println(ctx.getChild(0).getText());//{���
		}else if(check){//check = true���
			check = false;	//�ƹ��͵� ��¾Ƚ�Ű�� false�� ��ȯ������
		}		
	}

	@Override
	public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
		if(else_stmt) depth--;//else��� depth�� �ϳ� �ٿ���(������  'else'����ϰ� depth���󺹱ͽ����ֱ� ������ ��� X )
	}
	@Override
	public void enterArgs(MiniGoParser.ArgsContext ctx) {
		if(ctx.getChildCount()==0)//()�� ���
		//	System.out.println("()");
			System.out.println();
		if(ctx.getChildCount()>0 && !print){//()�� �ƴ� ���			
			System.out.println(ctx.getChild(0).getText());
			if(ctx.getChildCount()>1){//(a)�� �ƴ϶� (i int�̷��� �ִ� ���)
							
				System.out.println(ctx.getChild(1).getText()+ctx.getChild(2).getText());
				
			}
		}
		if(print){//fmt.print�� ���
			print = false;
			System.out.println();
		}
	}

	@Override
	public void exitArgs(MiniGoParser.ArgsContext ctx) {
	
	}

	@Override
	public void enterLocal_decl(MiniGoParser.Local_declContext ctx) {
		for(int i = 0 ; i < depth; i++){//local�̹Ƿ� ��ø�� �󸶳� �Ǿ��ִ��� �Ǵ��� ....�� �����
			System.out.print("....");
		}
		if(ctx.getChildCount() == 6){//var ans [15] int �̷� �� ������ ��
			System.out.print(ctx.getChild(0).getText()+" ");
			System.out.print(ctx.getChild(1).getText()+" ");
			System.out.print(ctx.getChild(2).getText());
			System.out.print(ctx.getChild(3).getText());
			System.out.print(ctx.getChild(4).getText()+" ");
			System.out.print(ctx.getChild(5).getText()+" ");
		}else{//������ ���
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
		if(ctx.getChildCount()>1){	//param�� ���� �� param�� i int�� ���		
			System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText());
		}else{//param�� ���� �� ó���� �ϳ��� ���� x, i int�� ����̹Ƿ� ,�� ��½�Ŵ
			System.out.print(ctx.getChild(0).getText()+", ");
		}
		//newTexts.put(ctx, s1+" "+s2);
	}

	@Override
	public void exitParam(MiniGoParser.ParamContext ctx) {
		
	}
	@Override 
	public void enterParams(MiniGoParser.ParamsContext ctx) {//param�� ���� ��
		System.out.print("(");//�Ұ�ȣ  ���
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
		System.out.print(")");//param���� �� �Ұ�ȣ ���
		
	}
	
	@Override
	public void enterFor_stmt(MiniGoParser.For_stmtContext ctx) {
		System.out.print(ctx.getChild(0)+" ");//'for' ���
		depth++;//depth����
		if(ctx.getChild(1).getChildCount() == 6){// for ; ; �� ���
			for_loop2 = true;
		}else{	//for binary�� ���
			for_loop = true;
		}
		//System.out.print(ctx.getChild(0).getText());
	}

	@Override
	public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
		for(int i =0; i<depth-1;i++){//���������� ....�� ����ְ�
			System.out.print("....");
		}
		depth--;//depth���ҽ�Ų ��
		System.out.print("}\n");//} ���
//		System.out.println(ctx.getChild(0).getText());
	}
	@Override
	public void enterExpr(MiniGoParser.ExprContext ctx) {
		//System.out.println("CHECK"+ctx.getChildCount());
		String s1 = null, s2 = null, op = null;
		if (isBinaryOperation(ctx)) {//binary�� ���
			
			s1 = newTexts.get(ctx.expr(0));
			s2 = newTexts.get(ctx.expr(1));
			op = ctx.getChild(1).getText();
			if(op.equals("=")&&ctx.getChild(2).getChildCount()>1){//binary�� �Ǵ��� �� = �� ������ ��쵵 �Ǵ��ϹǷ� ���ܽ����ش�.

					System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" ");//�տ� �ΰ��� ���
			}else if(return_val){//return�� ���
				return_val = false;//��½�Ű���ʰ� false�� ��ȯ��Ŵ
			}else if(ctx.getChild(0).getText().equals("(")){//��ȣ�� �����ϴ� ��� ( x+y )�� ���� ��
				braket = true;
				System.out.print(ctx.getChild(0).getText());//�� �� ��ȣ�� ��½�����(������ �������� �ٽ� binary�� ���� x + y�� ��µ�
			}
			else{
				if(check){//for�� if�� ���� �� �ٷ� ��ȣ�� �����
					System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+"{\n");
					
				}else{
					if(!for_loop&&!for_loop2){//�Ѵ� �ش����� ������	
						if(braket){//��ȣ�� �־����� false�� ��ȯ��Ű��, )�� ��½����־� (x + y)�� ���·� �ٲ�
							System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+")\n");
							braket = false;
						}else{//�ƴ϶��							
							System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+"\n");//�׳� ���
						}
						
					}
					if(for_loop2){//for_loop2�� ��� ';'�� �ް� ��½�Ŵ
						System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText()+";");
					}
					if(for_loop){//for_loop�̸� �׳���½�Ŵ
						System.out.print(ctx.getChild(0).getText()+" "+ctx.getChild(1).getText()+" "+ctx.getChild(2).getText());
						
					}
				}
			
				newTexts.put(ctx, s1 + " " + op + " " + s2);
		
			}
			
		}else if(ctx.getChildCount() == 2){//++�� ���
			s1 = ctx.getChild(0).getText();
			op = ctx.getChild(1).getText();
			if(!s1.equals("++")){
				if(!s1.equals("--")){					
					System.out.println(s1+op);
				}
			}
			
			newTexts.put(ctx, s1 + op );
			
		}else if(ctx.getChildCount() == 1){//�ܼ� expr
			s1 = ctx.getChild(0).getText();
		//	System.out.print(s1);
			
			newTexts.put(ctx, s1);
			
		}else if(ctx.getChildCount() == 4){//expr�� ���̰� 4�� ���� �����°�� �׳� ��½�Ŵ
			for(int i =0; i<ctx.getChildCount();i++){
				System.out.print(ctx.getChild(i).getText());
			}
		}else if(ctx.getChildCount() == 6){
			if(ctx.getChild(1).getText().equals(".")){//fmt.print�� ���
				print = true;
				for(int i = 0; i < ctx.getChildCount();i++){
					System.out.print(ctx.getChild(i).getText());
				}
			}else{//�ƴ� ���
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
		if(ctx.getChildCount() == 6){//���������� var ans [15] int�� ���
			System.out.print(ctx.getChild(0).getText()+" ");
			System.out.print(ctx.getChild(1).getText()+" ");
			System.out.print(ctx.getChild(2).getText());
			System.out.print(ctx.getChild(3).getText());
			System.out.print(ctx.getChild(4).getText()+" ");
			System.out.print(ctx.getChild(5).getText()+" ");
		}else{//�ƴ� ���
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
		for(int i = 0;i<depth;i++){//stmt�� ������ ��
				if(!check){
					if(!for_loop){						
						System.out.print("....");//"...."�� ��½�Ŵ
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
		for(int i =0; i < ctx.getChildCount();i++){//return value�� ��½�Ŵ	
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
