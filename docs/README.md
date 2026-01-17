# Documentation

## Media Files

### Screenshots

- **main-karaoke-player.png** - Main karaoke player app showing synchronized lyrics
- **ui-library-demo.png** - UI library demo with configuration panel
- **settings-screen.png** - Settings screen with color and animation options

### Videos

- **karaoke-demo.webm** - Full demonstration of karaoke functionality
- **ui-customization-demo.webm** - UI customization and effects demonstration

## Project Structure

```
docs/
├── images/
│   ├── main-karaoke-player.png     # Main app screenshot
│   ├── ui-library-demo.png         # Demo app screenshot
│   └── settings-screen.png         # Settings interface
└── videos/
    ├── karaoke-demo.webm            # Main functionality demo
    └── ui-customization-demo.webm   # Customization features demo
```

## Viewing Videos

The WebM videos can be viewed:
1. Directly in GitHub by clicking on them
2. In any modern browser
3. In VLC or other media players
4. Convert to GIF using: `ffmpeg -i input.webm -vf "fps=10,scale=320:-1" output.gif`

## Screenshot Descriptions

### Main Karaoke Player
Shows the main karaoke application with:
- Real-time synchronized lyrics
- Character-by-character highlighting
- Playback controls
- Dark theme with green accent colors

### UI Library Demo
Demonstrates the configuration panel with:
- Multiple viewer type options
- Font size and weight controls
- Font family selection
- Real-time preview of changes

### Settings Screen
Shows the customization options:
- Dark mode toggle
- Color selection for lyrics and background
- Font size presets
- Animation toggles (enable/disable, blur effect)

## Creating Additional Media

For contributors adding new screenshots or videos:

### Screenshots
- Use consistent device frame (Pixel 6 recommended)
- Capture at 1080x2340 resolution
- Save as PNG format
- Name descriptively: `feature-name-screen.png`

### Videos
- Record at 30fps minimum
- Use WebM format for web compatibility
- Keep under 30 seconds for demos
- Show smooth interactions and animations