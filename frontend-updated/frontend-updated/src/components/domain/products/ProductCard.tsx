import React from "react";
import { ShoppingBag, BookOpen, Clock, Library } from "lucide-react";
import { Product } from "../../../types/product";
import { resolveFileUrl } from "../../../api/client";
import { Badge } from "../../ui/Badge";
import { Button } from "../../ui/Button";

export interface ProductCardProps {
  product: Product;
  onAddToCart?: (product: Product) => void;
  onRentToCart?: (product: Product) => void;
  onBorrow?: (product: Product) => void;
  onSelect?: (product: Product) => void;
  isOwned?: boolean;
  hasActiveSubscription?: boolean;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  onAddToCart,
  onRentToCart,
  onBorrow,
  onSelect,
  isOwned = false,
  hasActiveSubscription = false,
}) => {
  const imageUrl = resolveFileUrl(product.coverImage);

  return (
    <div className="group relative bg-white dark:bg-slate-900 rounded-3xl p-4 shadow-sm hover:shadow-2xl transition-all duration-500 border border-slate-100 dark:border-slate-800/80 flex flex-col h-full hover:-translate-y-1.5">
      {/* BADGES */}
      <div className="absolute top-6 left-6 z-10 flex flex-wrap gap-1">
        {product.isRentable && <Badge variant="warning">Rentable</Badge>}
        {product.isLibraryEligible && <Badge variant="info">Library Pass</Badge>}
        {isOwned && <Badge variant="gold">In Shelf</Badge>}
      </div>

      {/* COVER IMAGE */}
      <div
        onClick={() => onSelect && onSelect(product)}
        className="aspect-[2/3] overflow-hidden rounded-2xl mb-4 bg-slate-100 dark:bg-slate-800 relative cursor-pointer shadow-inner"
      >
        <img
          src={imageUrl}
          alt={product.title}
          className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-700 ease-out"
          onError={(e: any) => {
            e.target.src = "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=400&auto=format&fit=crop&q=60";
          }}
        />
        <div className="absolute inset-0 bg-slate-950/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center backdrop-blur-[2px]">
          <span className="bg-white text-slate-950 px-5 py-2.5 rounded-full font-bold text-xs shadow-xl hover:bg-amber-500 hover:text-slate-950 transition-colors">
            Quick Details
          </span>
        </div>
      </div>

      {/* CONTENT */}
      <div className="space-y-2 mt-auto flex-1 flex flex-col justify-between">
        <div>
          <p className="text-xs font-bold text-amber-500 uppercase tracking-widest">
            {product.genreName || product.subcategoryName || "Literature"}
          </p>
          <h3
            onClick={() => onSelect && onSelect(product)}
            className="font-serif font-bold text-lg text-slate-900 dark:text-white leading-tight group-hover:text-amber-500 transition-colors line-clamp-2 cursor-pointer mt-1"
          >
            {product.title}
          </h3>
          {product.shortDescription && (
            <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 mt-1.5 leading-relaxed">
              {product.shortDescription}
            </p>
          )}
        </div>

        {/* PRICE & ACTION BUTTONS */}
        <div className="pt-4 border-t border-slate-100 dark:border-slate-800/80 space-y-3 mt-3">
          <div className="flex items-center justify-between">
            <div>
              <span className="text-[10px] uppercase text-slate-400 font-bold tracking-wider">Buy Price</span>
              <p className="text-lg font-serif font-bold text-slate-900 dark:text-white">
                ₹{product.price.toLocaleString("en-IN")}
              </p>
            </div>
            {product.isRentable && product.rentRate && (
              <div className="text-right">
                <span className="text-[10px] uppercase text-amber-500 font-bold tracking-wider">Rent Rate</span>
                <p className="text-xs font-bold text-amber-500">
                  ₹{product.rentRate}/day
                </p>
              </div>
            )}
          </div>

          <div className="flex flex-col gap-2">
            {hasActiveSubscription && product.isLibraryEligible && onBorrow && (
              <Button
                variant="gold"
                size="sm"
                className="w-full font-bold shadow-md"
                onClick={() => onBorrow(product)}
                leftIcon={<Library size={14} />}
              >
                Borrow with Pass
              </Button>
            )}

            <div className="flex gap-2">
              {onAddToCart && (
                <Button
                  variant={hasActiveSubscription && product.isLibraryEligible ? "outline" : "gold"}
                  size="sm"
                  className="flex-1"
                  onClick={() => onAddToCart(product)}
                  leftIcon={<ShoppingBag size={14} />}
                >
                  Buy
                </Button>
              )}

              {product.isRentable && onRentToCart && (
                <Button
                  variant="outline"
                  size="sm"
                  className="flex-1 border-amber-500/50 text-amber-600 dark:text-amber-400 hover:bg-amber-500/10"
                  onClick={() => onRentToCart(product)}
                  leftIcon={<Clock size={14} />}
                >
                  Rent
                </Button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
