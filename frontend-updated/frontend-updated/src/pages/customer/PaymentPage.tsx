import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { CreditCard, QrCode, Lock, CheckCircle2 } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import { useCart } from "../../hooks/useCart";
import { purchaseCheckoutAPI, rentCheckoutAPI } from "../../services/checkout.service";
import { clearCartAPI } from "../../services/cart.service";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import toast from "react-hot-toast";

export const PaymentPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { items, lastPurchaseType, purchaseTotal, rentalTotal, clear } = useCart();
  const [paymentMethod, setPaymentMethod] = useState<"upi" | "card">("upi");
  const [upiId, setUpiIdState] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const amountToPay = lastPurchaseType === "buy" ? purchaseTotal : rentalTotal;

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user?.userId) {
      toast.error("User session expired. Please sign in again.");
      return;
    }

    setIsLoading(true);
    try {
      const hasPurchase = items.some((item) => item.intent !== "RENT");
      const hasRent = items.some((item) => item.intent === "RENT");

      if (lastPurchaseType === "buy" && hasPurchase) {
        await purchaseCheckoutAPI(user.userId);
      } else if (lastPurchaseType === "rent" && hasRent) {
        await rentCheckoutAPI(user.userId);
      } else {
        if (hasPurchase) await purchaseCheckoutAPI(user.userId);
        if (hasRent) await rentCheckoutAPI(user.userId);
      }

      await clearCartAPI(user.userId);
      clear();

      toast.success("Payment completed successfully!");
      navigate("/success");
    } catch (err: any) {
      toast.error(err.message || "Transaction failed. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#FDFCFB] dark:bg-slate-950 py-12 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-900 border border-slate-100 dark:border-slate-800 rounded-3xl p-8 shadow-2xl space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-amber-500/10 text-amber-500 flex items-center justify-center mx-auto">
            <Lock size={24} />
          </div>
          <h2 className="text-2xl font-serif font-bold text-slate-900 dark:text-white">
            Secure Payment
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-widest">
            Total Amount Due
          </p>
          <p className="text-3xl font-serif font-bold text-amber-500">
            ₹{amountToPay.toLocaleString("en-IN")}
          </p>
        </div>

        {/* PAYMENT METHOD TOGGLE */}
        <div className="grid grid-cols-2 gap-3 p-1 bg-slate-100 dark:bg-slate-800 rounded-2xl">
          <button
            type="button"
            onClick={() => setPaymentMethod("upi")}
            className={`py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-2 ${
              paymentMethod === "upi"
                ? "bg-white dark:bg-slate-900 text-slate-900 dark:text-white shadow-sm"
                : "text-slate-500"
            }`}
          >
            <QrCode size={16} /> UPI Payment
          </button>
          <button
            type="button"
            onClick={() => setPaymentMethod("card")}
            className={`py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-2 ${
              paymentMethod === "card"
                ? "bg-white dark:bg-slate-900 text-slate-900 dark:text-white shadow-sm"
                : "text-slate-500"
            }`}
          >
            <CreditCard size={16} /> Card Payment
          </button>
        </div>

        <form onSubmit={handlePayment} className="space-y-4">
          {paymentMethod === "upi" ? (
            <Input
              label="Virtual Payment Address (VPA / UPI ID)"
              placeholder="username@upi"
              value={upiId}
              onChange={(e) => setUpiIdState(e.target.value)}
              leftIcon={<QrCode size={18} />}
              required
            />
          ) : (
            <>
              <Input
                label="Card Number"
                placeholder="4532 •••• •••• 8912"
                value={cardNumber}
                onChange={(e) => setCardNumber(e.target.value)}
                leftIcon={<CreditCard size={18} />}
                required
              />
              <div className="grid grid-cols-2 gap-3">
                <Input label="Expiry Date" placeholder="MM/YY" required />
                <Input label="CVV" placeholder="•••" type="password" required />
              </div>
            </>
          )}

          <Button
            type="submit"
            variant="gold"
            size="lg"
            className="w-full mt-4"
            isLoading={isLoading}
            rightIcon={<CheckCircle2 size={18} />}
          >
            Pay & Confirm Order
          </Button>
        </form>
      </div>
    </div>
  );
};
