'use client';

import React, { useEffect, useState } from 'react';
import { adminApi, AdminUser } from '@/modules/admin/api/admin.api';

export default function AdminUsersPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.listUsers()
      .then(res => setUsers(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="text-app-accent font-medium">Carregando...</div>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-app-ink">Gestão de Usuários (Admin)</h1>
      <div className="bg-app-surface rounded-xl shadow-app-card overflow-hidden border border-app-border">
        <table className="min-w-full divide-y divide-app-border">
          <thead className="bg-app-surface-2">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-app-muted uppercase tracking-wider">Email</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-app-muted uppercase tracking-wider">Role</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-app-muted uppercase tracking-wider">Data de Criação</th>
            </tr>
          </thead>
          <tbody className="bg-app-surface divide-y divide-app-border">
            {users.map(user => (
              <tr key={user.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-app-ink">{user.email}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-app-muted">{user.role?.name}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-app-muted">{new Date(user.createdAt).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
