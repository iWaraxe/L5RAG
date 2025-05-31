#!/bin/bash

# Script to update all branches to Spring AI 1.0.0

echo "Starting Spring AI 1.0.0 migration for all branches..."

# Store the current branch
CURRENT_BRANCH=$(git branch --show-current)
echo "Current branch: $CURRENT_BRANCH"

# List of branches to update (in order)
BRANCHES=(
    "01-rag-introduction"
    "02-vector-store-setup"
    "03-document-ingestion-and-splitting"
    "04-add-controller-and-service"
    "05-query-vector-store"
    "06-embedding-and-prompt-clarification"
    "07-qa-service-with-questionansweradvisor"
    "08-retrieval-augmentation-advance"
    "09-vectorstore-metadata-and-filtering"
)

# Function to update a branch
update_branch() {
    local branch=$1
    echo ""
    echo "========================================"
    echo "Updating branch: $branch"
    echo "========================================"
    
    # Checkout the branch
    git checkout $branch
    
    # Update pom.xml with Spring AI 1.0.0
    cp pom-1.0.0.xml pom.xml
    
    # Check if application.yml exists and needs updating
    if [ -f "src/main/resources/application.yml" ]; then
        echo "Updating application.yml for schema initialization..."
        # Add initialize-schema property if not present
        if ! grep -q "initialize-schema:" src/main/resources/application.yml; then
            # This is a simple approach - for complex YAML updates, consider using yq
            echo "Note: Please manually add 'initialize-schema: true' to pgvector configuration"
        fi
    fi
    
    # Commit the changes
    git add pom.xml
    git commit -m "Update to Spring AI 1.0.0

- Updated spring-ai.version from 1.0.0-M7 to 1.0.0
- All Spring AI dependencies now use 1.0.0
- Ready for Spring AI 1.0.0 migration

🤖 Generated with Claude Code"
    
    echo "Branch $branch updated successfully!"
}

# Create a backup tag before starting
echo "Creating backup tag before migration..."
git tag -a "backup-before-spring-ai-1.0.0-$(date +%Y%m%d-%H%M%S)" -m "Backup before Spring AI 1.0.0 migration"

# Update each branch
for branch in "${BRANCHES[@]}"; do
    update_branch "$branch"
done

# Return to the original branch
echo ""
echo "Returning to original branch: $CURRENT_BRANCH"
git checkout $CURRENT_BRANCH

echo ""
echo "========================================"
echo "Migration script completed!"
echo "========================================"
echo ""
echo "Next steps:"
echo "1. Review the changes in each branch"
echo "2. Update the code to use Spring AI 1.0.0 APIs"
echo "3. Test each branch thoroughly"
echo "4. Push changes when ready"
echo ""
echo "Key changes to implement in code:"
echo "- Update QuestionAnswerAdvisor to use builder pattern"
echo "- Add schema initialization to application.yml"
echo "- Update vector store configuration"
echo "- Update metadata filtering syntax"