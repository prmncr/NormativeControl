package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.dtos.ErrorType;
import lombok.val;
import org.docx4j.wml.P;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class ParserTests extends TestsBase {
    @Test
    void incorrectSizeTest() {
        val parser = createParser("incorrectWidth.docx");
        parser.checkPageSize();
        Assert.notEmpty(parser.errors, "Error not found!");
        Assert.state(parser.errors.get(0).errorType() == ErrorType.INCORRECT_PAGE_SIZE
                && parser.errors.get(0).paragraph() == -1
                && parser.errors.get(0).run() == -1, "Wrong error!");
    }

    @Test
    void incorrectMarginTest() {
        val parser = createParser("incorrectMargin.docx");
        parser.checkPageMargins();
        Assert.notEmpty(parser.errors, "Error not found!");
        Assert.state(parser.errors.get(0).errorType() == ErrorType.INCORRECT_PAGE_MARGINS
                && parser.errors.get(0).paragraph() == -1
                && parser.errors.get(0).run() == -1, "Wrong error!");
    }

    @Test
    void correctSectorsFound() {
        val parser = createParser("correctSectors.docx");
        parser.findSectors();
        parser.detectNodes();
        Assert.notEmpty(parser.nodes.get(0).getContent(), "0 not found!");
        Assert.notEmpty(parser.nodes.get(1).getContent(), "1 not found!");
        Assert.notEmpty(parser.nodes.get(2).getContent(), "2 not found!");
        Assert.notEmpty(parser.nodes.get(3).getContent(), "3 not found!");
        Assert.notEmpty(parser.nodes.get(4).getContent(), "4 not found!");
        Assert.notEmpty(parser.nodes.get(5).getContent(), "5 not found!");
        Assert.notEmpty(parser.nodes.get(6).getContent(), "6 not found!");
        Assert.notEmpty(parser.nodes.get(7).getContent(), "7 not found!");
        Assert.isTrue(parser.errors.size() == 0, "There shouldn't be any error!");
    }

    @Test
    void incorrectSectorsFound() {
        val parser = createParser("skippedSector.docx");
        parser.findSectors();
        Assert.isTrue(parser.nodes.size() == 7, "2 sectors must be found!");
        Assert.notEmpty(parser.nodes.get(0).getContent(), "0 must be found!");
        Assert.notEmpty(parser.nodes.get(1).getContent(), "1 must be found!");
        Assert.notEmpty(parser.nodes.get(2).getContent(), "2 must be found!");
        Assert.notEmpty(parser.nodes.get(3).getContent(), "3 must be found!");
        Assert.notEmpty(parser.nodes.get(4).getContent(), "4 must be found!");
        Assert.notEmpty(parser.nodes.get(5).getContent(), "5 must be found!");
        Assert.notEmpty(parser.nodes.get(6).getContent(), "6 must be found!");
        Assert.isTrue(parser.errors.size() == 1, "There should be error!");
    }

    @Test
    void allSectorsSkipped() {
        val parser = createParser("skippedAllSectors.docx");
        parser.findSectors();
        Assert.isTrue(parser.nodes.size() == 2, "2 sectors must be found!");
        Assert.notEmpty(parser.nodes.get(0).getContent(), "0 must be found!");
        Assert.notEmpty(parser.nodes.get(1).getContent(), "1 must be found!");
        Assert.isTrue(parser.errors.size() == 1, "There should be error!");
    }

    @Test
    void headerDetectedWithoutLineBreak() {
        val parser = createParser("headerWithoutLineBreak.docx");
        parser.findSectors();
        Assert.notEmpty(parser.nodes.get(0).getContent(), "0 not found!");
        Assert.notEmpty(parser.nodes.get(1).getContent(), "1 not found!");
    }

    @Test
    void headerDetectedWithLineBreak() {
        val parser = createParser("headerWithLineBreak.docx");
        parser.findSectors();
        Assert.notEmpty(parser.nodes.get(0).getContent(), "0 not found!");
        Assert.notEmpty(parser.nodes.get(1).getContent(), "1 not found!");
    }
}
