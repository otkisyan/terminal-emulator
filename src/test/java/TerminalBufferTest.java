import org.example.Attributes;
import org.example.Color;
import org.example.TerminalBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    @Test
    @DisplayName("setCursor() should move cursor to valid position within bounds")
    void setCursorShouldMoveToPosition() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(1, 2);
        assertEquals(1, buffer.getCursorRow());
        assertEquals(2, buffer.getCursorCol());
    }

    @Test
    @DisplayName("setCursor() should allow cursor to be set at screen boundaries")
    void setCursorAtScreenBoundaries() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(0, 0);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        buffer.setCursor(0, 4);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());

        buffer.setCursor(2, 0);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        buffer.setCursor(2, 4);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    @DisplayName("setCursor() should clamp cursor within screen boundaries")
    void setCursorShouldRespectBounds() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(-10, -10);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        buffer.setCursor(100, 100);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    @DisplayName("moveUp/moveDown/moveLeft/moveRight should move cursor correctly within bounds")
    void moveCursorShouldUpdatePosition() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(1, 2);

        buffer.moveUp(1);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(2, buffer.getCursorCol());

        buffer.moveDown(2);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(2, buffer.getCursorCol());

        buffer.moveLeft(1);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(1, buffer.getCursorCol());

        buffer.moveRight(2);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(3, buffer.getCursorCol());
    }

    @Test
    @DisplayName("moveUp/moveDown/moveLeft/moveRight should allow cursor to be moved to screen boundaries")
    void moveCursorFromZeroToEdges() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.setCursor(0, 0);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        buffer.moveRight(4);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());

        buffer.moveDown(2);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());

        buffer.moveLeft(4);
        assertEquals(2, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

        buffer.moveUp(2);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorCol());

    }

    @Test
    @DisplayName("moveUp/moveDown/moveLeft/moveRight should clamp cursor within screen boundaries")
    void moveCursorShouldRespectBounds() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.moveUp(5);
        assertEquals(0, buffer.getCursorRow());

        buffer.moveDown(10);
        assertEquals(2, buffer.getCursorRow());

        buffer.moveLeft(10);
        assertEquals(0, buffer.getCursorCol());

        buffer.moveRight(10);
        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    @DisplayName("write() should overwrite existing characters at cursor position")
    void writeShouldOverwriteCharacters() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.write("HELLO");
        buffer.setCursor(0, 0);
        buffer.write("X");

        assertEquals("XELLO", buffer.getLine(0, false));
        buffer.write("XXXX");
        assertEquals("XXXXX", buffer.getLine(0, false));
    }

    @Test
    @DisplayName("write() should wrap to next line when reaching screen width")
    void writeShouldWrapToNextLineWhenWidthExceeded() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.write("ABCDEFG");

        assertEquals("ABCDE", buffer.getLine(0, false));
        assertEquals("FG   ", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("write() should scroll screen up when writing beyond screen height")
    void writeShouldScrollWhenHeightExceeded() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");

        assertEquals("456", buffer.getLine(0, false));
        assertEquals("789", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("insert() should shift characters to the right from cursor position")
    void insertShouldShiftCharactersRight() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.write("HELLO");
        buffer.setCursor(0, 1);
        buffer.insert("X");

        assertEquals("HXELL", buffer.getLine(0, false));
    }

    @Test
    @DisplayName("insert() at last column should overwrite character (no shift possible)")
    void insertAtLastColumnShouldOverwrite() {
        TerminalBuffer buffer = new TerminalBuffer(5, 3, 10);

        buffer.write("HELLO");
        buffer.setCursor(0, 4);
        buffer.insert("X");

        assertEquals("HELLX", buffer.getLine(0, false));
    }

    @Test
    @DisplayName("insert() should wrap text to next line when width exceeded")
    void insertShouldWrapToNextLine() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.insert("ABCDE");

        assertEquals("ABC", buffer.getLine(0, false));
        assertEquals("DE ", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("insertEmptyLineAtBottom() should scroll top line into scrollback and add empty line at bottom")
    void insertEmptyLineAtBottomShouldScroll() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("ABC");
        buffer.write("DEF");

        buffer.insertEmptyLineAtBottom();

        assertEquals("ABC", buffer.getLine(0, true));

        assertEquals("DEF", buffer.getLine(0, false));
        assertEquals("   ", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("fillLine() should replace entire row with given character")
    void fillLineShouldReplaceEntireRow() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.fillLine(0, 'X');

        assertEquals("XXXXX", buffer.getLine(0, false));
    }

    @Test
    @DisplayName("scrollUp() should move top screen line into scrollback")
    void scrollbackShouldStoreScrolledLines() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");

        assertEquals("123", buffer.getLine(0, true));
        assertEquals("456", buffer.getLine(0, false));
        assertEquals("789", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("scrollUp() should discard oldest lines when exceeding scrollbackMax")
    void scrollbackShouldRespectMaxSize() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 1);

        buffer.write("123456789000");

        assertEquals("456", buffer.getLine(0, true));
    }

    @Test
    @DisplayName("clearScreen() should clear visible screen but keep scrollback")
    void clearScreenShouldResetScreenOnly() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.write("HELLO\nWORLD");

        assertEquals("HELLO", buffer.getLine(0, true));

        buffer.clearScreen();

        assertEquals("     ", buffer.getLine(0, false));
        assertEquals("     ", buffer.getLine(1, false));

        assertEquals("HELLO", buffer.getLine(0, true));
    }

    @Test
    @DisplayName("clearAll() should clear both screen and scrollback")
    void clearAllShouldClearScrollbackToo() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");

        assertEquals("123", buffer.getLine(0, true));

        buffer.clearAll();

        assertEquals("   ", buffer.getLine(0, false));
        assertEquals("   ", buffer.getLine(1, false));

        assertEquals("", buffer.getLine(0, true));
    }

    @Test
    @DisplayName("getCharAt() should return correct character from screen")
    void getCharAtShouldReturnCorrectChar() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.write("HELLOWORLD");

        assertEquals('H', buffer.getCharAt(0, 0, false));
        assertEquals('O', buffer.getCharAt(0, 4, false));
        assertEquals('W', buffer.getCharAt(1, 0, false));
        assertEquals('D', buffer.getCharAt(1, 4, false));
    }

    @Test
    @DisplayName("getLine() should return correct line from screen")
    void getLineFromScreen() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.write("HELLO");
        buffer.write("WORLD");

        assertEquals("HELLO", buffer.getLine(0, false));
        assertEquals("WORLD", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("getLine() should return correct line from scrollback")
    void getLineFromScrollback() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");

        assertEquals("123", buffer.getLine(0, true));
        assertEquals("456", buffer.getLine(0, false));
        assertEquals("789", buffer.getLine(1, false));
    }

    @Test
    @DisplayName("getLine() should return empty string for out-of-bounds indices")
    void getLineOutOfBounds() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123");

        assertEquals("", buffer.getLine(-1, false));
        assertEquals("", buffer.getLine(10, false));
        assertEquals("", buffer.getLine(-1, true));
        assertEquals("", buffer.getLine(5, true));
    }

    @Test
    @DisplayName("getAttributesAt() should return correct attributes from screen")
    void getAttributesFromScreen() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.setAttributes(Color.RED, Color.BLACK, true, false, false);
        buffer.write("A");

        Attributes attr = buffer.getAttributesAt(0, 0, false);

        assertNotNull(attr);
        assertEquals(Color.RED, attr.fg);
        assertEquals(Color.BLACK, attr.bg);
        assertTrue(attr.bold);
        assertFalse(attr.italic);
        assertFalse(attr.underline);
    }

    @Test
    @DisplayName("getAttributesAt() should return null for out-of-bounds indices")
    void getAttributesOutOfBounds() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        assertNull(buffer.getAttributesAt(-1, 0, false));
        assertNull(buffer.getAttributesAt(0, -1, false));
        assertNull(buffer.getAttributesAt(10, 0, false));
        assertNull(buffer.getAttributesAt(0, 10, false));
        assertNull(buffer.getAttributesAt(0, 0, true));
    }

    @Test
    @DisplayName("getScreenContent() should return all screen lines as string")
    void getScreenContentTest() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.write("HELLO");
        buffer.write("WORLD");

        String expected = "HELLO\nWORLD\n";
        assertEquals(expected, buffer.getScreenContent());
    }

    @Test
    @DisplayName("getScreenContent() should reflect cleared screen")
    void getScreenContentAfterClear() {
        TerminalBuffer buffer = new TerminalBuffer(5, 2, 10);

        buffer.write("HELLO\nWORLD");
        buffer.clearScreen();

        String expected = "     \n     \n";
        assertEquals(expected, buffer.getScreenContent());
    }

    @Test
    @DisplayName("getFullContent() should return scrollback + screen")
    void getFullContentWithScrollback() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");

        String expected = "123\n456\n789\n";
        assertEquals(expected, buffer.getFullContent());
    }

    @Test
    @DisplayName("getFullContent() should reflect cleared screen and scrollback")
    void getFullContentAfterClearAll() {
        TerminalBuffer buffer = new TerminalBuffer(3, 2, 10);

        buffer.write("123456789");
        buffer.clearAll();

        String expected = "   \n   \n";
        assertEquals(expected, buffer.getFullContent());
    }
}



