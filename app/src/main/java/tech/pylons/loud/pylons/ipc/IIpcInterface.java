package tech.pylons.loud.pylons.ipc;

/*
 * This file is auto-generated.  DO NOT MODIFY.
 */

// Declare any non-default types here with import statements

public interface IIpcInterface extends android.os.IInterface {
    /**
     * Default implementation for IIpcInterface.
     */
    public static class Default implements IIpcInterface {
        @Override
        public java.lang.String wallet2easel() throws android.os.RemoteException {
            return null;
        }

        @Override
        public void easel2wallet(java.lang.String json) throws android.os.RemoteException {
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IIpcInterface {
        private static final java.lang.String DESCRIPTOR = "tech.pylons.ipc.IIpcInterface";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an tech.pylons.ipc.IIpcInterface interface,
         * generating a proxy if needed.
         */
        public static IIpcInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IIpcInterface))) {
                return ((IIpcInterface) iin);
            }
            return new IIpcInterface.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_wallet2loud: {
                    data.enforceInterface(descriptor);
                    java.lang.String _result = this.wallet2easel();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_loud2wallet: {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.easel2wallet(_arg0);
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IIpcInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.lang.String wallet2easel() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_wallet2loud, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().wallet2easel();
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void easel2wallet(java.lang.String json) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(json);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_loud2wallet, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().easel2wallet(json);
                        return;
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public static IIpcInterface sDefaultImpl;
        }

        static final int TRANSACTION_wallet2loud = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_loud2wallet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        public static boolean setDefaultImpl(IIpcInterface impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Stub.Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IIpcInterface getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    public java.lang.String wallet2easel() throws android.os.RemoteException;

    public void easel2wallet(java.lang.String json) throws android.os.RemoteException;
}
