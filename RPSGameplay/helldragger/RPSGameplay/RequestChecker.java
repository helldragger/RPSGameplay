package helldragger.RPSGameplay;


import helldragger.RPSGameplay.Request.Operation;

import java.util.LinkedList;

import org.bukkit.scheduler.BukkitRunnable;


public class RequestChecker extends BukkitRunnable {

	protected static RequestChecker checker = new RequestChecker();

	private static LinkedList<Request> waitinglist = new LinkedList<Request>();

	private State state = State.waiting;

	enum State {
		waiting,working;
	}


	synchronized void examineRequest(Request req){
		if(Config.DEBUG_MODE) 
			RPSGPlugin.log.info("Examining request: "+ req.getOperation().name()+" pour "+req.getPlayer().getName()+" stats: "+ req.getData().keySet().toString() );
		if(req.getOperation() == Operation.ADDITION)
			StatsManager.getPlayerInfos(req.getPlayer()).addAllStatModifier(req.getData());
		else if(req.getOperation() == Operation.SOUSTRACTION)
			StatsManager.getPlayerInfos(req.getPlayer()).removeAllStatsModifiers(req.getData());

		waitinglist.remove(req);

	}

	@Override
	public void run() {
		this.state =  State.working;
		while(!waitinglist.isEmpty())
		{

			examineRequest(waitinglist.getFirst());			
		}
		try {
			
			synchronized (this){
				this.state = State.waiting;
				this.wait();
			}
		} catch (InterruptedException e) {
			System.out.print(this.getClass().getName() + " has been interrupted.");
		}
	}

	public static void registerRequest( Request req){
		getChecker();
		req.setID(waitinglist.size() + 1);
		waitinglist.add(req);
		if(!isWorking()){
			synchronized (checker){
				checker.notify();
			}
		}
	}

	private static RequestChecker getChecker(){
		if (checker == null)
			checker = new RequestChecker();

		return checker;
	}

	static boolean isWorking(){
		return (getChecker().state == State.working);
	}

	static void verifyRequest(){

		RequestChecker checker = getChecker();
		if(!isWorking()){
			synchronized (checker){
				checker.notify();
			}
		}
	}




}
