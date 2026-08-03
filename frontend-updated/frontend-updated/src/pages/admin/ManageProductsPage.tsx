import React, { useState } from "react";
import { Search, Edit, UploadCloud, FileSpreadsheet } from "lucide-react";
import { useFetch } from "../../hooks/useFetch";
import { browseProducts, updateProduct, getAllSubcategories, getAllGenres, getAllLanguages } from "../../services/product.service";
import { uploadCoverImage, uploadContentFile } from "../../services/upload.service";
import { bulkImportProducts } from "../../services/bulkImport.service";
import { Table, Column } from "../../components/ui/Table";
import { Pagination } from "../../components/ui/Pagination";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { Modal } from "../../components/ui/Modal";
import { FileUpload } from "../../components/ui/FileUpload";
import { Product, ProductRequest } from "../../types/product";
import { resolveFileUrl, FALLBACK_BOOK_IMAGE } from "../../api/client";
import toast from "react-hot-toast";

export const ManageProductsPage: React.FC = () => {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [formState, setFormState] = useState<Partial<ProductRequest>>({});
  const [isSaving, setIsSaving] = useState(false);
  const [isBulkImportOpen, setIsBulkImportOpen] = useState(false);
  const [isUploading, setIsUploading] = useState(false);

  const { data: pageData, isLoading, refetch } = useFetch(
    () => browseProducts({ page, size: 10, keyword: keyword || undefined }),
    [page, keyword]
  );

  const { data: subcategories } = useFetch(getAllSubcategories, []);
  const { data: genres } = useFetch(getAllGenres, []);
  const { data: languages } = useFetch(getAllLanguages, []);

  const handleStartEdit = (product: Product) => {
    setEditingProduct(product);
    setFormState({
      subcategoryId: product.subcategoryId,
      genreId: product.genreId,
      languageId: product.languageId,
      title: product.title,
      shortDescription: product.shortDescription,
      description: product.description,
      price: product.price,
      pages: product.pages,
      duration: product.duration,
      coverImage: product.coverImage,
      filePath: product.filePath,
      fileType: product.fileType || "PDF",
      isRentable: product.isRentable,
      isLibraryEligible: product.isLibraryEligible,
      rentRate: product.rentRate,
      minRentDays: product.minRentDays,
      isAvailable: product.isAvailable,
    });
  };

  const handleSaveProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProduct) return;
    setIsSaving(true);
    try {
      await updateProduct(editingProduct.productId, formState as ProductRequest);
      toast.success("Product updated successfully!");
      setEditingProduct(null);
      refetch();
    } catch (err: any) {
      toast.error(err.message || "Failed to update product");
    } finally {
      setIsSaving(false);
    }
  };

  const handleCoverUpload = async (file: File) => {
    setIsUploading(true);
    try {
      const url = await uploadCoverImage(file);
      setFormState((prev) => ({ ...prev, coverImage: url }));
      toast.success("Cover image uploaded!");
    } catch (err: any) {
      toast.error("Failed to upload cover image");
    } finally {
      setIsUploading(false);
    }
  };

  const handleContentUpload = async (file: File) => {
    setIsUploading(true);
    try {
      const url = await uploadContentFile(file);
      setFormState((prev) => ({ ...prev, filePath: url }));
      toast.success("Content PDF uploaded!");
    } catch (err: any) {
      toast.error("Failed to upload content PDF");
    } finally {
      setIsUploading(false);
    }
  };

  const handleBulkImportFile = async (file: File) => {
    setIsUploading(true);
    try {
      const res = await bulkImportProducts(file);
      toast.success(`Import complete! ${res.successCount} succeeded, ${res.failureCount} failed.`);
      setIsBulkImportOpen(false);
      refetch();
    } catch (err: any) {
      toast.error(err.message || "Bulk import failed");
    } finally {
      setIsUploading(false);
    }
  };

  const columns: Column<Product>[] = [
    {
      key: "coverImage",
      header: "Cover",
      render: (p) => (
        <img
          src={resolveFileUrl(p.coverImage)}
          alt={p.title}
          className="w-10 h-14 object-cover rounded shadow-sm bg-slate-800"
          onError={(e: any) => {
            e.target.src = FALLBACK_BOOK_IMAGE;
          }}
        />
      ),
    },
    {
      key: "title",
      header: "Title",
      render: (p) => (
        <div>
          <p className="font-bold text-white text-sm">{p.title}</p>
          <p className="text-xs text-amber-400">{p.genreName || p.subcategoryName}</p>
        </div>
      ),
    },
    {
      key: "price",
      header: "Price",
      render: (p) => <span className="font-bold text-white">₹{p.price}</span>,
    },
    {
      key: "actions",
      header: "Manage",
      render: (p) => (
        <Button
          variant="outline"
          size="sm"
          onClick={() => handleStartEdit(p)}
          leftIcon={<Edit size={14} />}
        >
          Edit & Files
        </Button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-serif font-bold text-white">Catalog CMS</h1>
          <p className="text-sm text-slate-400 mt-1">Manage books, attach cover images/PDFs, bulk import</p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="gold"
            size="md"
            onClick={() => setIsBulkImportOpen(true)}
            leftIcon={<FileSpreadsheet size={18} />}
          >
            Bulk Import Excel
          </Button>
        </div>
      </div>

      <div className="flex gap-4">
        <Input
          placeholder="Search catalog by title..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          leftIcon={<Search size={18} />}
        />
      </div>

      <Table
        columns={columns}
        data={pageData?.content || []}
        keyExtractor={(p) => p.productId}
        isLoading={isLoading}
      />

      <Pagination
        currentPage={page}
        totalPages={pageData?.totalPages || 0}
        totalElements={pageData?.totalElements}
        onPageChange={(newPage) => setPage(newPage)}
      />

      {/* EDIT MODAL */}
      <Modal
        isOpen={!!editingProduct}
        onClose={() => setEditingProduct(null)}
        title={`Edit Product #${editingProduct?.productId}`}
        maxWidth="xl"
      >
        {editingProduct && (
          <form onSubmit={handleSaveProduct} className="space-y-4 max-h-[75vh] overflow-y-auto pr-2">
            <Input
              label="Book Title"
              value={formState.title || ""}
              onChange={(e) => setFormState({ ...formState, title: e.target.value })}
              required
            />

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Price (₹)"
                type="number"
                value={formState.price || 0}
                onChange={(e) => setFormState({ ...formState, price: Number(e.target.value) })}
                required
              />
              <Input
                label="Pages"
                type="number"
                value={formState.pages || 0}
                onChange={(e) => setFormState({ ...formState, pages: Number(e.target.value) })}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FileUpload
                label="Upload Cover Image"
                accept="image/*"
                uploadedUrl={formState.coverImage}
                onFileSelect={handleCoverUpload}
                isUploading={isUploading}
              />
              <FileUpload
                label="Upload PDF Content File"
                accept="application/pdf"
                uploadedUrl={formState.filePath}
                onFileSelect={handleContentUpload}
                isUploading={isUploading}
              />
            </div>

            <div className="pt-4 flex justify-end gap-3">
              <Button variant="outline" type="button" onClick={() => setEditingProduct(null)}>
                Cancel
              </Button>
              <Button variant="gold" type="submit" isLoading={isSaving}>
                Save Product Changes
              </Button>
            </div>
          </form>
        )}
      </Modal>

      {/* BULK IMPORT MODAL */}
      <Modal
        isOpen={isBulkImportOpen}
        onClose={() => setIsBulkImportOpen(false)}
        title="Bulk Import Excel / CSV Catalog"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-400">
            Upload your master product spreadsheet. Automatically creates products & library packages.
          </p>
          <FileUpload
            label="Select Spreadsheet File (.xlsx, .csv)"
            accept=".xlsx,.csv"
            onFileSelect={handleBulkImportFile}
            isUploading={isUploading}
          />
        </div>
      </Modal>
    </div>
  );
};
