package be.vanin.controller;

import be.vanin.domain.BoardBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ben on 11/2/16.
 */
@RestController
public class PagesController {

    private String boardBooksLocation;
    private Collection<BoardBook> boardBooks;
    @Value("${boardbooks.endpoint.url}")
    private String endpointUrl;

    @Autowired
    public PagesController(@Value("${boardbooks.location}") String boardBooksLocation) {
        this.boardBooksLocation = boardBooksLocation;
        this.boardBooks = new ArrayList<>();

        String[] boardBooksDir = new File(boardBooksLocation).list();
        for (String boardBook : boardBooksDir) {
            File file = new File(boardBooksLocation, boardBook);
            if (file.isDirectory()) {
                String[] grades = file.list();
                for (String gradeStr : grades) {
                    try {
                        int grade = Integer.parseInt(gradeStr);
                        int maxPage = getMaxPage(boardBook, grade);
                        BoardBook bb = new BoardBook(boardBook, grade, 1, maxPage);
                        System.err.println(bb.toString());
                        this.boardBooks.add(bb);
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
            }
        }
    }

    @RequestMapping("/boardbook")
    public Collection<BoardBook> getBoardbooks() {
        return this.boardBooks;
    }

    @RequestMapping("/boardbook/{boardbook}/{grade}/start")
    public Map getStartPage(@PathVariable("boardbook") String boardBookName,
                            @PathVariable("grade") int grade) {
        BoardBook boardBook = find(boardBookName, grade);
        return createPageDescription(boardBook, grade, find(boardBookName, grade).getStartPage());
    }

    @RequestMapping("/boardbook/{boardbook}/{grade}/{page}")
    public Map getPageDescription(@PathVariable("boardbook") String boardBookName,
                                  @PathVariable("grade") int grade,
                                  @PathVariable("page") int page) {
        BoardBook boardBook = find(boardBookName, grade);
        return createPageDescription(boardBook, grade, page);
    }

    @RequestMapping("/boardbook/{boardbook}/{grade}/{page}/next")
    public Map getNextPage(@PathVariable("boardbook") String boardBookName,
                           @PathVariable("grade") int grade,
                           @PathVariable("page") int page) {
        BoardBook boardBook = find(boardBookName, grade);
        int nextPage = calculateNext(page, boardBook);
        return createPageDescription(boardBook, grade, nextPage);
    }

    @RequestMapping("/boardbook/{boardbook}/{grade}/{page}/previous")
    public Map getPreviousPage(@PathVariable("boardbook") String boardBookName,
                               @PathVariable("grade") int grade,
                               @PathVariable("page") int page) {
        BoardBook boardBook = find(boardBookName, grade);
        int previousPage = calculatePrevious(page, boardBook);
        return createPageDescription(boardBook, grade, previousPage);
    }


    private int calculateNext(int page, BoardBook boardBook) {
        int nextPage = page + 1;
        if (nextPage > boardBook.getEndPage()) {
            nextPage = page;
        }
        return nextPage;
    }

    private int calculatePrevious(int page, BoardBook boardBook) {
        int previousPage = page - 1;
        if (previousPage <= 0) {
            previousPage = boardBook.getStartPage();
        }
        return previousPage;
    }

    private Map createPageDescription(BoardBook boardBook, int grade, int page) {
        Map retVal = new HashMap();

        int next = calculateNext(page, boardBook);
        int previous = calculatePrevious(page, boardBook);

        retVal.put("pageNumber", page);
        retVal.put("leftAssetUrl", createAssetUrl(boardBook.getName(), grade, page));
        retVal.put("leftAssetCorrectionAssetUrl", createCorrectionAssetUrl(boardBook.getName(), grade, page));
        if (next != page) {
            retVal.put("rightAssetUrl", createAssetUrl(boardBook.getName(), grade, next));
            retVal.put("rightCorrectionAssetUrl", createCorrectionAssetUrl(boardBook.getName(), grade, next));
        }
        retVal.put("description", createUrl(boardBook.getName(), grade, page));

        next = calculateNext(calculateNext(page, boardBook), boardBook);
        if (next != page) {
            Map nextPageInfo = new HashMap();
            nextPageInfo.put("description", createUrl(boardBook.getName(), grade, next));
            retVal.put("next", nextPageInfo);
        }

        previous = calculatePrevious(calculatePrevious(page, boardBook), boardBook);
        if (previous != page) {
            Map previousPageInfo = new HashMap();
            previousPageInfo.put("description", createUrl(boardBook.getName(), grade, previous));
            retVal.put("previous", previousPageInfo);
        }
        return retVal;
    }

    private String createUrl(String boardBook, int grade, int page) {
        return endpointUrl + "boardbook/" + boardBook + "/" + grade + "/" + page;
    }

    private String createCorrectionAssetUrl(String boardBook, int grade, int page) {
        return endpointUrl + "assets/" + boardBook + "/" + grade + "/pages_correction/" + page + ".png";
    }

    private String createAssetUrl(String boardBook, int grade, int page) {
        return endpointUrl + "assets/" + boardBook + "/" + grade + "/pages/" + page + ".png";
    }

    private BoardBook find(String boardBook, int grade) {
        return boardBooks
                .stream()
                .filter(bb -> bb.getName().equals(boardBook) && bb.getGrade() == grade)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("boardbook '" + boardBook + "' with grade '" + grade + "' was not found"));
    }

    private int getMaxPage(String boardBook, int grade) {
        File boardBookDir = new File(boardBooksLocation + "/" + boardBook + "/" + grade + "/pages");
        String[] fileNames = boardBookDir.list();
        int maxPage = 0;
        for (String fileName : fileNames) {
            if (fileName.endsWith(".png")) {
                int pageNumber = Integer.parseInt(fileName.replace(".png", ""));
                maxPage = Math.max(pageNumber, maxPage);
            }
        }
        return maxPage;
    }


}
