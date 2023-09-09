package agent;

import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;
import viewer.Node;
import viewer.Viewer;

public class LoBoGamesAgent extends AI {
    protected int player = -1;
    private Game game;

    private static final int EVALUATION_PLAYOUTS = 25;
    private static final int MAX_PLAYOUT_DEPTH = 100;

    public static final float MAX = 1;
    public static final float MIN = -MAX;
    public static final float NEUTRAL = (MIN + MAX)/2;

    private Viewer viewer;

    public LoBoGamesAgent(Viewer viewer) {
        this.friendlyName = "LoBoGames";
        this.viewer = viewer;
    }

    @Override
    public Move selectAction(Game game, Context context, double maxSeconds, int maxIterations, int maxDepth) {
        FastArrayList<Move> legalMoves = game.moves(context).moves();
        if (!game.isAlternatingMoveGame())
            legalMoves = AIUtils.extractMovesForMover(legalMoves, player);

        Move bestMove = legalMoves.get(0);
        float bestScore = MIN;

        final long SEARCH_TIME_MILLIS = (long)maxSeconds*1000;
        long startTime = System.currentTimeMillis();
        long timeSpent = 0;
        int depth = 0;
        Node tree = new Node(context);
        while(timeSpent < SEARCH_TIME_MILLIS) {
            tree = new Node(context);
            bestMove = legalMoves.get(0);
            bestScore = MIN;
            for(Move move : legalMoves) {
                Context newContext = new Context(context);
                newContext.game().apply(newContext, move);
                float score = minimax(newContext, depth, MIN, MAX, false, tree);
                if(score > bestScore) {
                    bestMove = move;
                    bestScore = score;
                }
            }
            depth++;
            timeSpent = System.currentTimeMillis() - startTime;
        }
        tree.setEvaluation(bestScore);
        viewer.setTree(tree);
        return bestMove;
    }

    private float minimax(Context context, int depth, float alpha, float beta, boolean isMaximizing, Node father) {
        Node node = new Node(context);
        node.setFather(father);
        if(depth == 0 || context.trial().over()) {
            float evaluation = evaluate(context, isMaximizing, node);
            node.setEvaluation(evaluation);
            return evaluation;
        }
        FastArrayList<Move> legalMoves = game.moves(context).moves();
        if (!game.isAlternatingMoveGame())
            legalMoves = AIUtils.extractMovesForMover(legalMoves, player);
        if(isMaximizing) {
            float maxValue = MIN;
            for(Move move : legalMoves) {
                Context newContext = new Context(context);
				newContext.game().apply(newContext, move);
                float newValue = minimax(newContext, depth - 1, alpha, beta, false, node);
                maxValue = Math.max(maxValue, newValue);
                if(maxValue >= beta)
                    break;
                alpha = Math.max(alpha, maxValue);
            }
            node.setEvaluation(maxValue);
            return maxValue;
        }
        else {
            float minValue = MAX;
            for(Move move : legalMoves) {
                Context newContext = new Context(context);
                newContext.game().apply(newContext, move);
                float newValue = minimax(newContext, depth - 1, alpha, beta, true, node);
                minValue = Math.min(minValue, newValue);
                if(minValue <= alpha)
                    break;
                beta = Math.min(beta, minValue);
            }
            node.setEvaluation(minValue);
            return minValue;
        }
    }
    private float evaluate(Context context, boolean isMaximizing, Node node) {
        if(context.trial().over())
            return evaluateTerminalState(context);
        int startingPlayer = player;
        if(!isMaximizing)
            startingPlayer = 1 - player;
        return evaluateWithPlayouts(context, startingPlayer, node);
    }

    private float evaluateTerminalState(Context context) {
        if(context.winners().contains(this.player))
            return MAX;
        if(!context.winners().isEmpty())
            return MIN;
        return NEUTRAL;
    }

    private float evaluateWithPlayouts(Context context, int startingPlayer, Node node) {
        float evaluation = 0f;
        for(int p = 0; p < EVALUATION_PLAYOUTS; p++)
            evaluation += makePlayout(context, startingPlayer);
        node.setPlayouts((int)evaluation, EVALUATION_PLAYOUTS);
        return evaluation/EVALUATION_PLAYOUTS;
    }

    private float makePlayout(Context context, int startingPlayer) {
		Context newContext = new Context(context);
		int currentPlayer = startingPlayer;
        int depth = 0;
		while(!newContext.trial().over() && depth < MAX_PLAYOUT_DEPTH) {
			Move move = getRandomMove(newContext, currentPlayer);
			newContext.game().apply(newContext, move);
			currentPlayer = 1 - currentPlayer;
            depth++;
		}
		return evaluateTerminalState(newContext);
	}

    private Move getRandomMove(Context context, int player) {
		FastArrayList<Move> legalMoves = game.moves(context).moves();
		
		if (!game.isAlternatingMoveGame())
			legalMoves = AIUtils.extractMovesForMover(legalMoves, player);
		
		final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
		return legalMoves.get(r);
	}

    @Override
	public void initAI(final Game game, final int playerID)
	{
        this.game = game;
		this.player = playerID;
	}
}
