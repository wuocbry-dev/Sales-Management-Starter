function RegisterPage() {
  return (
    <div className="space-y-5">
      <div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">Đăng ký cửa hàng</h2>
        <p className="text-sm text-slate-500 mt-1">
          Màn placeholder. Sau này có thể mở rộng onboarding tenant/branch/store.
        </p>
      </div>

      <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
        <p className="text-sm font-black text-slate-900 mb-3">Suggested fields</p>
        <ul className="text-sm text-slate-600 space-y-1 list-disc pl-5">
          <li>Owner full name</li>
          <li>Phone number</li>
          <li>Email</li>
          <li>Store name</li>
          <li>Business type</li>
          <li>Province / city</li>
          <li>Password</li>
        </ul>
      </div>
    </div>
  );
}

export default RegisterPage;
