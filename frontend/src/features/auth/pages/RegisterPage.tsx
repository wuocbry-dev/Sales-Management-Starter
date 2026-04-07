function RegisterPage() {
  return (
    <div>
      <h2>Register Store</h2>
      <p className="muted">
        This starter keeps registration as a placeholder screen. You can extend it with
        tenant, branch, and store onboarding later.
      </p>

      <div className="card">
        <p><strong>Suggested fields:</strong></p>
        <ul>
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
