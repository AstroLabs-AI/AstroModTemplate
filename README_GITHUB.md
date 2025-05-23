# GitHub Setup Instructions

## Repository Setup

1. **Create a new GitHub repository**:
   - Go to https://github.com/new
   - Name: `ArcaneCodex`
   - Description: "Advanced Futuristic Magic-Tech Mod for Minecraft Forge 1.20.1"
   - Make it public
   - Don't initialize with README (we already have one)

2. **Connect local repository to GitHub**:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/ArcaneCodex.git
   git branch -M main
   git push -u origin main
   git push origin forge-1.20.1
   ```

3. **Set up GitHub Secrets** (if needed for releases):
   - Go to Settings → Secrets and variables → Actions
   - Add any required secrets (usually GITHUB_TOKEN is automatic)

## Automated Features

### 1. Automatic Building
- Every push to `main` or `forge-1.20.1` triggers a build
- Build artifacts are uploaded for each commit
- Failed builds will show ❌ in commit history

### 2. Automatic Releases
- Create a tag to trigger a release: `git tag v0.2.0 && git push origin v0.2.0`
- GitHub Actions will:
  - Build the mod
  - Generate changelog from commits
  - Create a release with the JAR file
  - Add installation instructions

### 3. Auto-commit Script
- Run `./auto-commit.sh` to automatically commit with descriptive messages
- The script analyzes changed files and creates meaningful commit messages
- Useful for regular development commits

### 4. Scheduled Auto-commits
- GitHub Actions runs every 6 hours to check for uncommitted changes
- Helps ensure work is regularly saved
- Can be triggered manually from Actions tab

## Development Workflow

1. **Make changes** to the mod
2. **Test locally** with `./gradlew runClient`
3. **Auto-commit**: `./auto-commit.sh`
4. **Push**: `git push`
5. **Wait for build** to complete on GitHub
6. **Create release** when ready: `git tag vX.Y.Z && git push origin vX.Y.Z`

## Build Status Badge

Add this to your README.md:
```markdown
![Build Status](https://github.com/YOUR_USERNAME/ArcaneCodex/workflows/Build%20and%20Release/badge.svg)
```

## Troubleshooting

- **Build fails**: Check Java version (needs 17)
- **Release not created**: Ensure tag follows `vX.Y.Z` format
- **Permissions error**: Check repository settings and workflow permissions