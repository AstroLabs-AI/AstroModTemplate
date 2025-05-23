#!/bin/bash

# Auto-commit script for Arcane Codex development
# This script automatically commits changes with descriptive messages

cd "$(dirname "$0")"

# Function to generate commit message based on changed files
generate_commit_message() {
    local changed_files=$(git diff --name-only --cached)
    local message="Development progress: "
    
    if echo "$changed_files" | grep -q "src/main/java.*blocks"; then
        message+="blocks, "
    fi
    if echo "$changed_files" | grep -q "src/main/java.*items"; then
        message+="items, "
    fi
    if echo "$changed_files" | grep -q "src/main/java.*research"; then
        message+="research system, "
    fi
    if echo "$changed_files" | grep -q "src/main/java.*particles"; then
        message+="particles, "
    fi
    if echo "$changed_files" | grep -q "src/main/java.*capabilities"; then
        message+="capabilities, "
    fi
    if echo "$changed_files" | grep -q "src/main/resources"; then
        message+="resources, "
    fi
    if echo "$changed_files" | grep -q "\.md$"; then
        message+="documentation, "
    fi
    
    # Remove trailing comma and space
    message=${message%, }
    
    # Add file count
    local file_count=$(echo "$changed_files" | wc -l)
    message+="\n\n- Modified $file_count files"
    
    echo -e "$message"
}

# Check if there are changes
if [[ -n $(git status -s) ]]; then
    echo "Changes detected, preparing commit..."
    
    # Stage all changes
    git add -A
    
    # Generate commit message
    commit_msg=$(generate_commit_message)
    
    # Commit changes
    git commit -m "$commit_msg"
    
    echo "Changes committed successfully!"
    
    # Optional: push to remote
    # git push origin $(git branch --show-current)
else
    echo "No changes to commit."
fi