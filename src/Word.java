
public class Word {
    private int type;
    private String word;
    private int row;

    public Word() {
    }

    // 单词
    public Word(int type, String word, int row) {
        this.type = type;
        this.word = word;
        this.row = row;
    }

    public void setPair(int type, String word, int row){
        this.type = type;
        this.word = word;
        this.row = row;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public String toString()
    {
        return "(" + type + "," + word + ")";
    }

}
