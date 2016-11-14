package be.vanin.domain;

/**
 * Created by ben on 11/2/16.
 */
public class BoardBook {

    private String name;
    private int grade;
    private int startPage;
    private int endPage;

    public BoardBook(String name, int grade, int startPage, int endPage) {
        this.name = name;
        this.grade = grade;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardBook boardBook = (BoardBook) o;

        if (grade != boardBook.grade) return false;
        return name.equals(boardBook.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + grade;
        return result;
    }

    @Override
    public String toString() {
        return "BoardBook{" +
                "name='" + name + '\'' +
                ", grade=" + grade +
                ", startPage=" + startPage +
                ", endPage=" + endPage +
                '}';
    }
}
