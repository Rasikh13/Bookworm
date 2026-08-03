export interface Product {
  productId: number;
  subcategoryId: number;
  subcategoryName: string;
  genreId?: number;
  genreName?: string;
  languageId?: number;
  languageName?: string;
  title: string;
  shortDescription?: string;
  description?: string;
  price: number;
  pages?: number;
  duration?: number;
  coverImage?: string;
  filePath?: string;
  fileType?: string;
  isRentable: boolean;
  isLibraryEligible: boolean;
  rentRate?: number;
  minRentDays?: number;
  isAvailable: boolean;
}

export interface Genre {
  genreId: number;
  genreName: string;
  description?: string;
}

export interface Language {
  languageId: number;
  languageName: string;
}

export interface Subcategory {
  subcategoryId: number;
  subcategoryName: string;
  categoryId?: number;
}

export interface ProductRequest {
  subcategoryId: number;
  genreId?: number;
  languageId?: number;
  beneficiaryId?: number;
  title: string;
  shortDescription?: string;
  description?: string;
  price: number;
  pages?: number;
  duration?: number;
  coverImage?: string;
  filePath?: string;
  fileType?: string;
  isRentable?: boolean;
  isLibraryEligible?: boolean;
  rentRate?: number;
  minRentDays?: number;
  isAvailable?: boolean;
}
