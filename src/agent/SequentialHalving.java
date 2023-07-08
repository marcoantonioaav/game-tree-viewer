package agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;
import viewer.Node;
import viewer.Viewer;

public class SequentialHalving extends AI {
	protected int player = -1;

	private final int BUDGET = 5000;
	private final int VICTORIES = 0;
	private final int PLAYOUTS = 1;
	private HashMap<Move, int[]> movesDict;

    private Viewer viewer;
	
	public SequentialHalving(Viewer viewer) {
		this.friendlyName = "Sequential Halving Agent";
		this.viewer = viewer;
	}

	private Move getRandomMove(Game game, Context context, int player) {
		FastArrayList<Move> legalMoves = game.moves(context).moves();
		
		if (!game.isAlternatingMoveGame())
			legalMoves = AIUtils.extractMovesForMover(legalMoves, player);
		
		final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
		return legalMoves.get(r);
	}

	protected int makePlayout(Game game, Context context, int startingPlayer) {
		Context newContext = new Context(context);
		int currentPlayer = startingPlayer;
		while(!newContext.trial().over()) {
			Move move = getRandomMove(game, newContext, currentPlayer);
			newContext.game().apply(newContext, move);
			currentPlayer = 1 - currentPlayer;
		}
		if(newContext.winners().contains(this.player))
			return 1;
		return 0;
	}

	@Override
	public Move selectAction
	(
		final Game game, 
		final Context context, 
		final double maxSeconds,
		final int maxIterations,
		final int maxDepth
	)
	{
        Node tree = new Node(context);
		FastArrayList<Move> legalMoves = game.moves(context).moves();
		if (!game.isAlternatingMoveGame())
			legalMoves = AIUtils.extractMovesForMover(legalMoves, player);
		movesDict = new HashMap<>();
		for(Move legalMove : legalMoves) {
			movesDict.put(legalMove, new int[]{0, 0});
		}
		while(movesDict.size() > 1) {
			for(Move move : movesDict.keySet()) {
				Context newContext = new Context(context);
				newContext.game().apply(newContext, move);
				int playouts = (int)Math.floor(BUDGET/(movesDict.size()*Math.ceil(log2(legalMoves.size()))));
				for(int i = 0; i < playouts; i++) {
					movesDict.get(move)[VICTORIES] += makePlayout(game, newContext, 1 - player);
				}
				movesDict.get(move)[PLAYOUTS] += playouts;
			}
			List<Move> moves = new ArrayList<Move>(movesDict.keySet());
			Collections.sort(moves, new Comparator<Move>() {
				@Override
				public int compare(Move o1, Move o2) {
					float f1 = (float)movesDict.get(o1)[VICTORIES]/(float)movesDict.get(o1)[PLAYOUTS];
					float f2 = (float)movesDict.get(o2)[VICTORIES]/(float)movesDict.get(o2)[PLAYOUTS];
					return Float.compare(f1, f2);
				}
			});
			for(int i = 0; i < Math.floor(movesDict.size()/2); i++) {
				Context newContext = new Context(context);
				newContext.game().apply(newContext, moves.get(i));
				tree.addChild(new Node(newContext, movesDict.get(moves.get(i))[VICTORIES], movesDict.get(moves.get(i))[PLAYOUTS]));
				movesDict.remove(moves.get(i));
			}
		}
		Move move = (Move) movesDict.keySet().toArray()[0];
		Context newContext = new Context(context);
		newContext.game().apply(newContext, move);
		tree.addChild(new Node(newContext, movesDict.get(move)[VICTORIES], movesDict.get(move)[PLAYOUTS]));
        viewer.setTree(tree);
		return move;
	}

	protected int log2(int n)
    {
        return (int)(Math.log(n) / Math.log(2));
    }
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
	}
}
