package bacon.move;

public class DefaultIllegalMove extends Move {

    public DefaultIllegalMove() {
        super(-1,null,null,-1,-1,-1);

    }

    @Override
    public boolean isLegal() {
        return false;
    }

    @Override
    public void doMove() {

    }
}
