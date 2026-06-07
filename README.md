# Terminal emulator

## Intro
**Terminal text buffer** — the core data structure that terminal emulators use to store and manipulate displayed text.

When a shell sends output, the terminal emulator updates this buffer, and the UI renders it.

A terminal buffer consists of a grid of character cells. Each cell can have:
- Character (or empty)
- Foreground color: default, or one of 16 standard terminal colors
- Background color: default, or one of 16 standard terminal colors
- Style flags: bold, italic, underline (at minimum)

The buffer maintains a cursor position — where the next character will be written.

The buffer has two logical parts:
- Screen — the last N lines that fit the screen dimensions (e.g., 80×24). This is the editable part and what users see.
- Scrollback — lines that scrolled off the top of the screen, preserved for history and unmodifiable. Users can scroll up to view them.

**Terminal buffer requirements:**

Setup
- Configurable initial width and height
- Configurable scrollback maximum size (number of lines)

Attributes
- Set current attributes: foreground, background and styles. These attributes should be used for further edits.

Cursor
- Get/set cursor position (column, row)
- Move cursor: up, down, left, right by N cells
- Cursor must not move outside screen bounds

Editing

Operations that take the current cursor position and attributes into account:

- Write a text on a line, overriding the current content. Moves the cursor.
- Insert a text on a line, possibly wrapping the line. Moves the cursor.
- Fill a line with a character (or empty)

Operations that do not depend on cursor position or attributes:
- Insert an empty line at the bottom of the screen
- Clear the entire screen
- Clear the screen and scrollback

Content Access
- Get character at position (from screen and scrollback)
- Get attributes at position (from screen and scrollback)
- Get line as string (from screen and scrollback)
- Get entire screen content as string
- Get entire screen+scrollback content as string
