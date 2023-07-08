package agent;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;
import viewer.Node;
import viewer.Viewer;

public class Minimax extends AI {
    protected int player = -1;
    private Game game;

    public static final int DEPTH = 2;

    public static final float MAX = 1;
    public static final float MIN = -MAX;
    public static final float NEUTRAL = (MIN + MAX)/2;

    private Viewer viewer;

    public Minimax(Viewer viewer) {
        this.friendlyName = "Minimax";
        this.viewer = viewer;
    }

    @Override
    public Move selectAction(Game game, Context context, double maxSeconds, int maxIterations, int maxDepth) {
        Node tree = new Node(context);
        FastArrayList<Move> legalMoves = game.moves(context).moves();
        if (!game.isAlternatingMoveGame())
            legalMoves = AIUtils.extractMovesForMover(legalMoves, player);

        Move bestMove = legalMoves.get(0);
        float bestScore = MIN;
        for(Move move : legalMoves) {
            Context newContext = new Context(context);
			newContext.game().apply(newContext, move);
            float score = minimax(newContext, DEPTH, MIN, MAX, false, tree);
            if(score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }
        tree.setEvaluation(bestScore);
        viewer.setTree(tree);
        return bestMove;
    }

    private float minimax(Context context, int depth, float alpha, float beta, boolean isMaximizing, Node father) {
        Node node = new Node(context);
        node.setFather(father);
        if(depth == 0 || context.trial().over()) {
            float evaluation = evaluate(context);
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
    private float evaluate(Context context) {
        if(context.trial().over()) {
            if(context.winners().contains(this.player))
                return MAX;
            if (!context.winners().isEmpty())
                return MIN;
        }
        return NEUTRAL;
    }

    @Override
	public void initAI(final Game game, final int playerID)
	{
        this.game = game;
		this.player = playerID;
	}
}
