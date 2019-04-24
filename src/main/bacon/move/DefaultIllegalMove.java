package bacon.move;

public class DefaultIllegalMove extends Move {

    public DefaultIllegalMove() {
        super(null, null, -1, -1);
    }

    @Override
    public boolean isLegal() {
        return false;
    }

    @Override
    public void doMove() {}

    @Override
    public byte[] encodeBinary() {
        throw new UnsupportedOperationException();
    }
}
