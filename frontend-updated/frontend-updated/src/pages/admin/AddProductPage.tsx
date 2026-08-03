import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PlusCircle, ArrowLeft, Award } from "lucide-react";
import { useFetch } from "../../hooks/useFetch";
import { createProduct, getAllSubcategories, getAllGenres, getAllLanguages } from "../../services/product.service";
import { getAllBeneficiaries } from "../../services/beneficiary.service";
import { uploadCoverImage, uploadContentFile } from "../../services/upload.service";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { FileUpload } from "../../components/ui/FileUpload";
import { ProductRequest } from "../../types/product";
import toast from "react-hot-toast";

export const AddProductPage: React.FC = () => {
  const navigate = useNavigate();
  const [formState, setFormState] = useState<Partial<ProductRequest>>({
    subcategoryId: 1,
    genreId: undefined,
    languageId: 1,
    beneficiaryId: 1,
    title: "",
    shortDescription: "",
    description: "",
    price: 299,
    pages: 200,
    duration: 0,
    isRentable: true,
    rentRate: 29,
    minRentDays: 7,
    isLibraryEligible: true,
    isAvailable: true,
    fileType: "PDF",
  });

  const [isSaving, setIsSaving] = useState(false);
  const [isUploading, setIsUploading] = useState(false);

  const { data: subcategories } = useFetch(getAllSubcategories, []);
  const { data: genres } = useFetch(getAllGenres, []);
  const { data: languages } = useFetch(getAllLanguages, []);
  const { data: beneficiaries } = useFetch(() => getAllBeneficiaries(true), []);

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
      toast.success("Content file uploaded!");
    } catch (err: any) {
      toast.error("Failed to upload content file");
    } finally {
      setIsUploading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formState.title || formState.price === undefined) {
      toast.error("Please fill in title and price");
      return;
    }
    if (!formState.languageId) {
      toast.error("Please select a language");
      return;
    }
    if (!formState.subcategoryId) {
      toast.error("Please select a subcategory");
      return;
    }

    setIsSaving(true);
    try {
      const payload: ProductRequest = {
        subcategoryId: formState.subcategoryId,
        genreId: formState.genreId || null,
        languageId: formState.languageId,
        beneficiaryId: formState.beneficiaryId || 1,
        title: formState.title,
        shortDescription: formState.shortDescription || "",
        description: formState.description || "",
        price: Number(formState.price),
        pages: formState.pages ? Number(formState.pages) : 0,
        duration: formState.duration ? Number(formState.duration) : 0,
        coverImage: formState.coverImage || "/Books-images/1984.jpg",
        filePath: formState.filePath || "/uploads/content/sample.pdf",
        fileType: formState.fileType || "PDF",
        isRentable: !!formState.isRentable,
        rentRate: formState.isRentable ? Number(formState.rentRate || 20) : null,
        minRentDays: formState.isRentable ? Number(formState.minRentDays || 7) : null,
        isLibraryEligible: !!formState.isLibraryEligible,
        isAvailable: formState.isAvailable !== false,
      };

      await createProduct(payload);
      toast.success("Product published with 10% royalty split configuration!");
      navigate("/admin/products");
    } catch (err: any) {
      toast.error(err.message || "Failed to publish product");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="max-w-3xl space-y-6">
      <div className="flex items-center gap-4 border-b border-slate-800 pb-4">
        <button
          onClick={() => navigate("/admin/products")}
          className="p-2 rounded-xl bg-slate-900 text-slate-400 hover:text-white"
        >
          <ArrowLeft size={20} />
        </button>
        <div>
          <h1 className="text-3xl font-serif font-bold text-white">Add New Book / Media</h1>
          <p className="text-sm text-slate-400 mt-1">Publish a new digital title & assign author/publisher royalties</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="bg-slate-900 border border-slate-800 rounded-3xl p-8 space-y-6">
        <Input
          label="Book Title"
          placeholder="e.g. Clean Architecture in Practice"
          value={formState.title || ""}
          onChange={(e) => setFormState({ ...formState, title: e.target.value })}
          required
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-400">Subcategory</label>
            <select
              value={formState.subcategoryId || 1}
              onChange={(e) => setFormState({ ...formState, subcategoryId: Number(e.target.value) })}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500"
            >
              {subcategories?.map((sub) => (
                <option key={sub.subcategoryId} value={sub.subcategoryId}>
                  {sub.subcategoryName}
                </option>
              ))}
              {!subcategories?.length && <option value={1}>eBooks</option>}
            </select>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-400">Genre</label>
            <select
              value={formState.genreId || ""}
              onChange={(e) => setFormState({ ...formState, genreId: e.target.value ? Number(e.target.value) : undefined })}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500"
            >
              <option value="">None / General</option>
              {genres?.map((g) => (
                <option key={g.genreId} value={g.genreId}>
                  {g.genreName}
                </option>
              ))}
            </select>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-400">Language</label>
            <select
              value={formState.languageId || 1}
              onChange={(e) => setFormState({ ...formState, languageId: Number(e.target.value) })}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500"
            >
              {languages?.map((lang) => (
                <option key={lang.languageId} value={lang.languageId}>
                  {lang.languageName}
                </option>
              ))}
              {!languages?.length && <option value={1}>English</option>}
            </select>
          </div>
        </div>

        {/* AUTHOR / PUBLISHER BENEFICIARY SELECTION */}
        <div className="bg-slate-950/60 p-4 rounded-2xl border border-slate-800/80 space-y-2">
          <div className="flex items-center gap-2">
            <Award className="text-amber-500" size={18} />
            <label className="text-xs font-bold uppercase tracking-wider text-amber-400">
              Author / Publisher Royalty Beneficiary (10% Royalty)
            </label>
          </div>
          <select
            value={formState.beneficiaryId || 1}
            onChange={(e) => setFormState({ ...formState, beneficiaryId: Number(e.target.value) })}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-sm text-white focus:outline-none focus:border-amber-500"
          >
            {beneficiaries?.map((ben) => (
              <option key={ben.beneficiaryId} value={ben.beneficiaryId}>
                {ben.name} ({ben.description || "Active Beneficiary"})
              </option>
            ))}
            {!beneficiaries?.length && <option value={1}>HarperCollins Authors Guild</option>}
          </select>
          <p className="text-[11px] text-slate-400 leading-tight">
            10% of every sale or rental price will automatically accrue as royalty to this beneficiary's ledger.
          </p>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Purchase Price (₹)"
            type="number"
            placeholder="499"
            value={formState.price !== undefined ? formState.price : ""}
            onChange={(e) => setFormState({ ...formState, price: Number(e.target.value) })}
            required
          />
          <Input
            label="Page Count"
            type="number"
            placeholder="350"
            value={formState.pages !== undefined ? formState.pages : ""}
            onChange={(e) => setFormState({ ...formState, pages: Number(e.target.value) })}
          />
        </div>

        <Input
          label="Short Description"
          placeholder="Brief summary of the book"
          value={formState.shortDescription || ""}
          onChange={(e) => setFormState({ ...formState, shortDescription: e.target.value })}
        />

        {/* RENTABLE & LIBRARY OPTIONS */}
        <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <span className="text-sm font-bold text-white">Enable Book Rental</span>
              <p className="text-xs text-slate-400">Allow users to rent this title by the day</p>
            </div>
            <input
              type="checkbox"
              checked={!!formState.isRentable}
              onChange={(e) => setFormState({ ...formState, isRentable: e.target.checked })}
              className="w-5 h-5 accent-amber-500 rounded cursor-pointer"
            />
          </div>

          {formState.isRentable && (
            <div className="grid grid-cols-2 gap-4 pt-2 border-t border-slate-800">
              <Input
                label="Rent Rate per Day (₹)"
                type="number"
                value={formState.rentRate || 25}
                onChange={(e) => setFormState({ ...formState, rentRate: Number(e.target.value) })}
              />
              <Input
                label="Min Rental Days"
                type="number"
                value={formState.minRentDays || 7}
                onChange={(e) => setFormState({ ...formState, minRentDays: Number(e.target.value) })}
              />
            </div>
          )}

          <div className="flex items-center justify-between pt-2 border-t border-slate-800">
            <div>
              <span className="text-sm font-bold text-white">Library Pass Eligible</span>
              <p className="text-xs text-slate-400">Allow active subscribers to borrow this title</p>
            </div>
            <input
              type="checkbox"
              checked={!!formState.isLibraryEligible}
              onChange={(e) => setFormState({ ...formState, isLibraryEligible: e.target.checked })}
              className="w-5 h-5 accent-amber-500 rounded cursor-pointer"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <FileUpload
            label="Cover Image File"
            accept="image/*"
            uploadedUrl={formState.coverImage}
            onFileSelect={handleCoverUpload}
            isUploading={isUploading}
          />
          <FileUpload
            label="PDF eBook Content File"
            accept="application/pdf"
            uploadedUrl={formState.filePath}
            onFileSelect={handleContentUpload}
            isUploading={isUploading}
          />
        </div>

        <Button
          type="submit"
          variant="gold"
          size="lg"
          className="w-full mt-4"
          isLoading={isSaving}
          leftIcon={<PlusCircle size={18} />}
        >
          Publish Product To Catalog
        </Button>
      </form>
    </div>
  );
};
