import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException{
        long start = System.currentTimeMillis();
        Scanner sc = new Scanner(System.in);

        //Accounts do not hold sensitive information.
        //Asking for password in this way is ok.
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String pw = sc.nextLine();

        Client client = new Client("imcs.svcs.cs.pdx.edu", 3589);
        client.login(username, pw);
        List<IMCSGame> gms = client.getGameList();
        for(int i = 0; i < gms.size(); ++i){
            System.out.println(gms.get(i).toString());
        }
		System.out.print("gameId to request: ");
        String gi = sc.nextLine();
        char c = client.accept(gi);

        System.out.println("I am " + c);

        Board brd = new Board();
        int depth = 9;
        while(true){
            if(c == 'W'){
                String me = brd.getIDMove(depth);
                brd.move(me);
                client.sendMove(me);

                String cl = client.getMove();
                if(cl == null){
                    System.err.println("null op move");
                    System.exit(0);
                }
                brd.move(cl);
            }
            else if(c == 'B'){
                String cl = client.getMove();
                if(cl == null){
                    System.err.println("op move null");
                    System.exit(0);
                }
                brd.move(cl);

                String me = brd.getIDMove(depth);
                brd.move(me);
                client.sendMove(me);

            }
			else{
				break;
			}
        }
        client.close();

    }
}
