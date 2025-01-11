#!/bin/bash

# Base directory structure
BASE_DIR="app/src/main"
PACKAGE_PATH="java/com/example/testproject"

# Create main directories
mkdir -p "$BASE_DIR/$PACKAGE_PATH"/{data/{model,repository,remote},domain/model,presentation/{home/{adapter,view,viewmodel},addproduct/{view,viewmodel}},utils}

# Create res directories
mkdir -p "$BASE_DIR/res"/{drawable,layout,navigation}

# Create resource files
touch "$BASE_DIR/res/drawable/"{ic_add.xml,placeholder_image.xml,bg_product_type.xml}
touch "$BASE_DIR/res/layout/"{activity_main.xml,fragment_home.xml,item_product.xml}
touch "$BASE_DIR/res/navigation/nav_graph.xml"

# Create Kotlin files
touch "$BASE_DIR/$PACKAGE_PATH/MainActivity.kt"
touch "$BASE_DIR/$PACKAGE_PATH/domain/model/Product.kt"
touch "$BASE_DIR/$PACKAGE_PATH/presentation/home/adapter/ProductAdapter.kt"
touch "$BASE_DIR/$PACKAGE_PATH/presentation/home/view/HomeFragment.kt"
touch "$BASE_DIR/$PACKAGE_PATH/presentation/home/viewmodel/HomeViewModel.kt"
touch "$BASE_DIR/$PACKAGE_PATH/presentation/addproduct/view/AddProductBottomSheet.kt"
touch "$BASE_DIR/$PACKAGE_PATH/presentation/addproduct/viewmodel/AddProductViewModel.kt"

# Print directory structure
echo "Created directory structure:"
tree "$BASE_DIR"